package com.zzw.zwm.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.zzw.zwm.database.MyDataBaseHelper;
import com.zzw.zwm.database.MyDataBaseHelper.ParameterException;

public class MyDataBaseTest {
	public static void main(String[] args){
		try {
			MyDataBaseHelper helper=new MyDataBaseHelper();
			// ����
			helper.createUserTable();
			helper.createEnjoyTable();
			helper.createEmailboxTable();
			// �������
			helper.insert("user", new String[]{"null", "'����'", "'z123456'", "true", "'2016-11-25-10-55-10'"});
			helper.insert("user", new String[]{"null", "'����'", "'l123456'", "false", "'2016-11-26-13-10-44'"});
			helper.insert("user", new String[]{"null", "'����'", "'w123456'", "true", "'2016-05-28-09-09-09'"});
			
			helper.insert("enjoy", new String[]{"'java'", "99", "50", "101"});
			helper.insert("enjoy", new String[]{"'back'", "101", "50", "99"});
			helper.insert("enjoy", new String[]{"'site'", "5", "303", "110"});
			
			helper.insert("emailbox", new String[]{"'����'", "'����'", "'java'", "'2016-11-25-10-55-10'"});
			helper.insert("emailbox", new String[]{"'����'", "'����'", "'back'", "'2016-11-26-13-10-44'"});
			helper.insert("emailbox", new String[]{"'����'", "'����'", "'site'", "'2016-05-28-09-09-09'"});
			// ��ѯ����
			ResultSet res=helper.query("user", null, null, null);
			if(res!=null){
				System.out.println("ID\tNAME\tPASSWORD\tONLINE\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t\t"+
							res.getString(4)+"\t"+res.getString(5));
				}
			}
			System.out.println("====================");
			res=helper.query("enjoy", null, null, null);
			if(res!=null){
				System.out.println("WORD\tYOUDAO\tBING\tBAIDU");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			System.out.println("====================");
			res=helper.query("emailbox", null, null, null);
			if(res!=null){
				System.out.println("SEND\tRECEIVE\tWORD\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			System.out.println("====================");
			// ��������
			helper.update("user", "password", "'a123456'", "name=?", new String[]{"'����'"});
			helper.update("enjoy", "bing", "60", "bing=?", new String[]{"50"});
			// ��ѯ����
			res=helper.query("user", null, null, null);
			if(res!=null){
				System.out.println("ID\tNAME\tPASSWORD\tONLINE\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t\t"+
							res.getString(4)+"\t"+res.getString(5));
				}
			}
			System.out.println("====================");
			res=helper.query("enjoy", null, null, null);
			if(res!=null){
				System.out.println("WORD\tYOUDAO\tBING\tBAIDU");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			System.out.println("====================");
			res=helper.query("emailbox", null, null, null);
			if(res!=null){
				System.out.println("SEND\tRECEIVE\tWORD\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			System.out.println("====================");
			// ɾ������
			helper.delete("user", "name=?", new String[]{"'����'"});
			helper.delete("enjoy", "bing=?", new String[]{"60"});
			// ��ѯ����
			res=helper.query("user", null, null, null);
			if(res!=null){
				System.out.println("ID\tNAME\tPASSWORD\tONLINE\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t\t"+
							res.getString(4)+"\t"+res.getString(5));
				}
			}
			System.out.println("====================");
			res=helper.query("enjoy", null, null, null);
			if(res!=null){
				System.out.println("WORD\tYOUDAO\tBING\tBAIDU");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			System.out.println("====================");
			res=helper.query("emailbox", null, null, null);
			if(res!=null){
				System.out.println("SEND\tRECEIVE\tWORD\tDATE");
				while(res.next()){
					System.out.println(res.getString(1)+"\t"+
							res.getString(2)+"\t"+
							res.getString(3)+"\t"+
							res.getString(4));
				}
			}
			helper.execute("drop table if exists emailbox;");
			helper.execute("drop table if exists user;");
			helper.execute("drop table if exists enjoy;");
		} catch (SQLException e1) {
			for(Throwable t : e1)
				t.printStackTrace();
		} catch (ParameterException e) {
			e.printStackTrace();
		}
	}
}
