/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mss.DAO;

import com.mss.model.*;
import com.mysql.jdbc.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;

/**
 *
 * @author Palaa
 */

public class DB {
   
    private static final String URL = "jdbc:mysql://localhost:3306/mssystem";
    private static final String USERNAME="root";
    private static final String PASSWORD="";
//    
      // private static final String URL = "jdbc:mysql://sql4.freemysqlhosting.net:3306/sql418593";
   // private static final String USERNAME="sql418593";
   // private static final String PASSWORD="yT2%iF7*"; 
    
    /**
     * Creates the connection to the database.
     **/
    
    private static Connection connect() throws Exception{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        return con;
    }
    

    private static boolean executeQuery(String sql) throws Exception{
        Connection con = connect();
        PreparedStatement p =con.prepareStatement(sql);
        p.executeUpdate();
        p.close();
        return true;
    }
 
    public static ResultSet getDBResult(String sql) throws Exception{
            Connection con = connect();
            ResultSet r = con.createStatement().executeQuery(sql);
            return r;
    }
    
    

    
    // todo : add session 
    
    public static boolean verifyLogin(String user,String pass) throws Exception{
        ResultSet r=getDBResult("SELECT user,pass FROM users WHERE user='"+user+"' AND pass='"+pass+"'");
        if(r.next()){
            return true;
        }else{
            return false;
        } 
    }
    
      /**
     * Search the database for a given item.
     **/
    public static Items searchItem(String itemName,int categoryID,int cityID) throws Exception{
        //System.out.println("333");
        String sql="SELECT name,price,address FROM item AS a,item_unit AS b,shop_item_unit_price AS c,shop AS d WHERE a.name='"+itemName+"'AND b.item_id=a.id AND c.item_unit_id=b.id AND d.id=c.shop_id;";
        ResultSet r=getDBResult(sql);
        ArrayList<Item> itemList= new ArrayList<Item>();
        while(r.next()){
            
            Item item=new Item(r.getString("name"), r.getDouble("price"), r.getString("address"));
            itemList.add(item);
        }
        Items items=new Items(itemList);
        return items;
    }

    
    
    public static boolean fetchingProfile(String Id) throws Exception{
        ResultSet r=getDBResult("SELECT user,pass FROM users WHERE user='"+Id+"'");
        if(r.next()){
            return true;
        }else{
            return false;
        }
    }

    
     /**
     * Changes the price of the given item in the given shop.
     **/
    public static void editPrice(int shopid, int id, double d) {
        try {
            String sql ="UPDATE shop_item_unit_price set price = "+d+" WHERE shop_id = "+shopid+" AND item_unit_id ="+id+";";
            System.out.println("SQL is : "+sql);
            executeQuery(sql);
          //  throw new UnsupportedOperationException("Not yet implemented");
        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Returns the item with the given id
     **/ 
    public static Item getItem(int id){
         try {
            String sql ="SELECT * FROM WHERE itemid='"+id+"';";
            System.out.println("SQL is : "+sql);
            ResultSet r = getDBResult(sql);
            if(r.next()){
                return new Item(r.getString("index"), r.getDouble("price"), r.getString("image"));
            }
          //  throw new UnsupportedOperationException("Not yet implemented");
        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    /**
     * Returns the list of categories available
     **/
    public static Categories getCategories() throws Exception{
        ResultSet r= DB.getDBResult("SELECT * FROM category;");
        ArrayList<Category> categoryList=new ArrayList<Category>(); 
        while(r.next()){ 
            Category category = new Category(r.getInt("id"),r.getString("name"));
            categoryList.add(category);
        } 
        
        Categories categories=new Categories(categoryList);
        return categories;
    }

 
    public static ShopResults getValidShops(ListItems items){
        
        int item_count = items.getListItems().size();
        
        ArrayList<ShopResult> list= new ArrayList<ShopResult>();
        ShopResults result = new ShopResults(list);
        
        
        
//        String item_id_sql =  "itemid = 2 OR "
//                            + "itemid = 3 OR "
//                            + "itemid = 4";
        String item_id_sql =  "";
        
        for (Iterator<ListItem> it = items.getListItems().iterator(); it.hasNext();) {
            
            ListItem item = it.next();
            item_id_sql = item_id_sql + "itemid = "+item.getId();
            
            if(it.hasNext()){
                item_id_sql = item_id_sql + " OR ";                
            }
        }
        
        String sql = "SELECT shopid AS a "
                +    "FROM shops "
                +    "WHERE ( "
                +       "SELECT count(*) "
                +       "FROM ( "
                +           "SELECT shop,item "
                +           "FROM shops "
                +           "WHERE "
                +           item_id_sql
                +           " ) "
                +           "GROUP BY shop_id WHERE shop_id = a "
                +       ") = "
                +       item_count
                +       ";" ;
        
        try {
            ResultSet r = getDBResult(sql);
            while(r.next()){
                ShopResult res = new ShopResult(r.getInt("shopid"), r.getString("shopname"), r.getString("shopaddress"));
                list.add(res);
            }
        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return  result;
    }
     
    
    
    
    public static ArrayList<Integer> minimalSearch(int id){
    
        ArrayList<Integer> result = new ArrayList<Integer>();
        String sql =  "SELECT shop_id FROM  shop_item_unit_price AS b , item_unit AS c "
                    + "WHERE c.item_id = "+id+" AND c.id = b.item_unit_id ;";
        
        try {
            ResultSet r = getDBResult(sql);
            while(r.next()){
                int shop_id = r.getInt("shop_id");
                result.add(shop_id);
            }
        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    
}