package com.chail.apputil.jdbc.jdbcutilsone;

public class TestJdbc {
	
	@org.junit.Test
	public void testOracle9i() throws Exception {
		String user="chail";
		String pass="chail";
		String url="jdbc:oracle:thin:@127.0.0.1:1521/pdb";
		JDBCUtil jdbcUtil = new JDBCUtil(url,JdbcDirver.ORACLE_DRIVER,user,pass);
		jdbcUtil.getConnection();
		String sql = "select *from city where countryCode = ?";
		jdbcUtil.executeQuery(sql);
		jdbcUtil.releaseConnectn();
	}
	
}
