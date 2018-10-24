package com.chail.apputil.hdfs;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class HdfsClinet {

	public static void main(String[] args) throws Exception {
		getfiles2();
	}

	
	
	public static void  getfiles() {
		try {
			Configuration conf = new Configuration();
			// 不设置该代码会出现错误：java.io.IOException: No FileSystem for scheme: hdfs
			conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
			String filePath = "hdfs://192.168.210.183:8020/user";
			Path path = new Path(filePath);
			// 这里需要设置URI，否则出现错误：java.lang.IllegalArgumentException: Wrong FS:
			FileSystem fs = FileSystem.get(new URI(filePath), conf);
			System.out.println("READING ============================");
			FileStatus[] files=fs.listStatus(path);
			for(FileStatus fileStatus:files) {
				System.out.println(fileStatus.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void getfiles2() throws Exception {
		String user = "hive/master@HADOOP.COM";
		String keytab = "D:/hive.keytab";
		String dir = "hdfs://192.168.200.18:8020/";
        Configuration conf = new Configuration();
    	//conf.addResource(new Path("D:/hdfs-site.xml"));
    	conf.set("hadoop.security.authentication", "kerberos");
    	//conf.addResource(new Path("D:/core-site.xml"));
        //System.setProperty("java.security.krb5.conf", "D:/krb5.ini");
//        System.setProperty("HADOOP_USER_NAME", "hive");
        UserGroupInformation.setConfiguration(conf);
       // UserGroupInformation.loginUserFromKeytab(user, keytab);
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fs = FileSystem.get(new URI(dir), conf);
        
		System.out.println("READING ============================");
		Path path = new Path(dir);
		FileStatus[] files=fs.listStatus(path);
		for(FileStatus fileStatus:files) {
			System.out.println(fileStatus.getPath());
		}
    }
 
	
}
