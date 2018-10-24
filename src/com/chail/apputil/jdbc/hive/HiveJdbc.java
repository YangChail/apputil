package com.chail.apputil.jdbc.hive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import com.chail.apputil.jdbc.jdbcutilsone.JDBCUtil;
import com.chail.apputil.jdbc.jdbcutilsone.JdbcDirver;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class HiveJdbc {

	public static void testHiveKb() throws Exception {
		/*String userkb = "hive/master@HADOOP.COM";
		String keytabkb = "D:/hive.keytab";
		Configuration conf = new Configuration();
		// conf.addResource(new Path("D:/hdfs-site.xml"));
		conf.set("hadoop.security.authentication", "kerberos");
		// conf.addResource(new Path("D:/core-site.xml"));
		System.setProperty("java.security.krb5.conf", "D:/krb5.ini");
		System.setProperty("HADOOP_USER_NAME", "HDFS");
		UserGroupInformation.setConfiguration(conf);
		UserGroupInformation.loginUserFromKeytab(userkb, keytabkb);*/
		Configuration conf = new Configuration();
		conf.set("hadoop.security.authentication", "kerberos");
		//UserGroupInformation.setConfiguration(conf);
		//System.setProperty("java.security.krb5.conf", "D:/krb5.ini");
		String user = "hive/master@HADOOP.COM";
		String pass = "hive";
		String url = "jdbc:hive2://192.168.200.18:10000/default;principal=hive/master@HADOOP.COM";
		JDBCUtil jdbcUtil = new JDBCUtil(url, JdbcDirver.HIVE_DRIVER, user, pass);
		jdbcUtil.getConnection();
		String sql = "show tables";
		List<Map<String, Object>> executeQuery = jdbcUtil.executeQuery(sql);
		System.out.println(executeQuery.size());
		jdbcUtil.releaseConnectn();
	}
	
	
	private static void copytab() throws Exception {
		String sql="create table many_tab.tb_";
		String sql2=" as select * from ";
		List<String> list=new ArrayList<String>();
		int j=0;
		for(int i=105;i<10000;i++) {
			String sqlaa=sql+ String.format("%05d", i)+sql2;
			String to="";
			if(j==0) {
				to="many_tab.tb_00011";
			}
			if(j==1) {
				to="many_tab.tb_00012";
			}
			if(j==2) {
				to="many_tab.tb_00013";
			}
			if(j==3) {
				to="many_tab.tb_00014";
			}
			if(j==4) {
				to="many_tab.tb_00015";
				j=-1;
			}
			j++;
			list.add(sqlaa+to);
		}
	    LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
	    ExecutorService pool =getPoll(2,queue);
		for (String sqls : list) {
			pool.execute(() -> {
				System.out.println(sqls);
				JDBCUtil jdbcUtil = getJdbc();
				jdbcUtil.getConnection();
				jdbcUtil.executeUpdate(sqls);
				//
				jdbcUtil.releaseConnectn();
			});
		}
		
		waitPoll(pool,queue);
	}
	
	
	
	
	public static void insertPartiton() {
		LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
	    ExecutorService pool =getPoll(2,queue);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String sql="insert into  many_part.big_part partition(dt='?') select * from many_tab.tb_0020";
		List<String> list=new ArrayList<String>();
		for(int i=0;i<1000;i++) {
			c .add(Calendar.DATE, 1);
			String date=sdf.format(c.getTime());
			if(i%2==0) {
				list.add(sql.replace("?", date)+"1");
				list.add(sql.replace("?", date)+"2");
			}
		}
		for (String sqls : list) {
			pool.execute(() -> {
				System.out.println(sqls);
				JDBCUtil jdbcUtil = getJdbc();
				jdbcUtil.getConnection();
				jdbcUtil.executeUpdate(sqls);
				//
				jdbcUtil.releaseConnectn();
			});
		}
		
	}
	
	public static JDBCUtil getJdbc() {
		String user = "hive";
		String pass = "hive";
		String url = "jdbc:hive2://192.168.241.104:10000/default";
		return new JDBCUtil(url, JdbcDirver.HIVE_DRIVER, user, pass);
	}
	
	public static void waitPoll(ExecutorService pool,LinkedBlockingQueue<Runnable> queue) {
		try {
			boolean loop = true;
			do { // 等待所有任务完成
					// 阻塞，直到线程池里所有任务结束
				loop = !pool.awaitTermination(10, TimeUnit.SECONDS);
				int size = queue.size();
				System.out.println(size);
			} while (loop);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static ExecutorService getPoll(int threadsize,LinkedBlockingQueue<Runnable> queue) {
		    ExecutorService pool = new  ThreadPoolExecutor(threadsize, threadsize,
	                20L, TimeUnit.SECONDS,
	                queue,new ThreadFactoryBuilder()
		            .setNameFormat("dm-hive-%d").build());
		    
		    return pool;
		    
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		//testHiveKb();
		//copytab();
		insertPartiton();
	}
}
