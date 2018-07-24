package com.chail.apputil.jdbc.jdbcutilsone;

public class Test {
	
	
	public void testOracle9i() throws Exception {
		String user="";
		String pass="";
		String url="";
		String driver="";
		JDBCUtil jdbcUtil = new JDBCUtil(url,driver,user,pass);
		jdbcUtil.getConnection();
		String sql = "select *from city where countryCode = ?";
		jdbcUtil.executeQuery(sql, null);
		jdbcUtil.releaseConnectn();
	}
	
}
