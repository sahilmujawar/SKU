/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sku;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author SAHIL
 */
public class SKU {

    /**
     * @param args the command line arguments
     */
    
    public static class ItemInput {
        String sql          = null;
        String prod_name    = null;
        String prod_type    = null;
        String identifier   = null;
        ResultSet rs        = null;
        int sku_quantity    = 0;
        int orders_quantity = 0;
        int prod_no         = 0;
        int amount          = 0;
        int dis_amt         = 0;
        int prod_amt        = 0;
        int total_amt       = 0;
        
        /*
        Function "final_purchase" is used for computation of discount on the basis of discount on product configured at
        SKU table and products ordered which is stored at ORDERS table.
        */
        void final_purchase(long seq_no,Query query) throws ClassNotFoundException, SQLException {
            System.out.println("\nProduct Name\tQuantity\tFinal Price");
            sql  = "select b.prod_name,quaantity,quantity,amount,dis_amt,a.prod_type from sku a,orders b ";
            sql += "where a.prod_no = b.prod_no and a.prod_name = b.prod_name and seq_no = '"+seq_no+"'";
            rs = query.fetchQueryDetails(sql);
            while(rs.next()) {
                prod_name       = rs.getString("b.prod_name");
                orders_quantity = rs.getInt("quaantity");
                sku_quantity    = rs.getInt("quantity");
                amount          = rs.getInt("amount");
                dis_amt         = rs.getInt("dis_amt");
                prod_type       = rs.getString("a.prod_type");
                if(prod_type.equals("S")) {
                    prod_amt = ((orders_quantity/sku_quantity) * dis_amt) + ((orders_quantity%sku_quantity) * amount);
                    System.out.println(prod_name+"\t\t"+orders_quantity+"\t\t"+prod_amt);
                    total_amt += prod_amt;
                }
                else
                {
                    sql  = "select b.prod_name,quaantity,quantity,amount,dis_amt,a.prod_type from sku a,orders b ";
                    sql += "where a.prod_no = b.prod_no and a.prod_name = b.prod_name and seq_no = '"+seq_no+"' ";
                    sql += "and prod_type<>'S' and ((select count(1) from sku where prod_type in ";
                    sql += "(select prod_type from sku where prod_name in (select prod_name from orders where seq_no=b.seq_no)) ";
                    sql += "and prod_type<>'S')=(select count(1) from orders where seq_no = b.seq_no and prod_name in ";
                    sql += "(select prod_name from sku where prod_type<>'S')))";
                    rs = query.fetchQueryDetails(sql);
                    while(rs.next()) {
                        identifier      = "A";
                        prod_name       = rs.getString("b.prod_name");
                        orders_quantity = rs.getInt("quaantity");
                        sku_quantity    = rs.getInt("quantity");
                        amount          = rs.getInt("amount");
                        dis_amt         = rs.getInt("dis_amt");
                        prod_type       = rs.getString("a.prod_type");
                        if(dis_amt != 0) {
                            System.out.println(prod_name+"\t\t"+orders_quantity+"\t\t"+dis_amt);
                            total_amt += dis_amt;
                        }                    
                    }
                    if(identifier == null) {
                        sql  = "select b.prod_name,quaantity,quantity,amount,dis_amt,a.prod_type from sku a,orders b ";
                        sql += "where a.prod_no = b.prod_no and a.prod_name = b.prod_name and seq_no = '"+seq_no+"' and prod_type<>'S'";
                        rs = query.fetchQueryDetails(sql);
                        while(rs.next()) {
                            prod_name       = rs.getString("b.prod_name");
                            orders_quantity = rs.getInt("quaantity");
                            sku_quantity    = rs.getInt("quantity");
                            amount          = rs.getInt("amount");
                            dis_amt         = rs.getInt("dis_amt");
                            prod_type       = rs.getString("a.prod_type");
                            System.out.println(prod_name+"\t\t"+orders_quantity+"\t\t"+amount); 
                            total_amt += amount;
                        }
                    }
                }
            }
                            query.dbConnectClose();
                            System.out.println("Total Price : "+total_amt);                            
        }
        
        /*
        Function "getInput" is used to display order details to user and get desirable input and store order details
        in ORDERS table.
        The products are displays through details maintained in SKU table which is independent of programming changes.
        You can add or remove any product from the table and the same will get reflected automatically.
        */        
        void getInput(Query query) throws ClassNotFoundException, SQLException {
            long sequenceNo = System.currentTimeMillis();
            Scanner scanner = new Scanner(System.in);
            int item = -1;
            while(item != 0) {
                sql = "select prod_no,prod_name,quantity,prod_type from sku";
                rs = query.fetchQueryDetails(sql);
                System.out.println("\nProduct No.\tProduct Name");
                while(rs.next()) {
                    prod_name = rs.getString("prod_name");
                    prod_no   = rs.getInt("prod_no");
                    System.out.println(prod_no+"\t\t"+prod_name);
                }
                System.out.println("Enter Product No. to purchase or 0 to end purchase and proceed:");
                item = scanner.nextInt();
                if(item == 0) {
                    System.out.println("Thank You for purchase");
                    final_purchase(sequenceNo,query);
                    System.exit(0);
                }
                System.out.println("Entered Product is "+item);
                System.out.println("\nEnter quantity :");
                int noOfItems = scanner.nextInt();
                System.out.println("Entered quantity is "+noOfItems);
                sql = "insert into sahil.orders select prod_no,prod_name,'"+noOfItems+"','"+sequenceNo+"' from sahil.sku where prod_no = '"+item+"'";
                query.executeInsert(sql);
                }
            }
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // TODO code application logic here
        ItemInput item = new ItemInput();
        item.getInput(new Query());
    }
}
