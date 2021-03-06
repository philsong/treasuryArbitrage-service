package com.bwq.treasuryArbitrage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bwq.treasuryArbitrage.common.ArbitrageCodes;
import com.bwq.treasuryArbitrage.common.LiveData;
import com.bwq.treasuryArbitrage.modelsCalculation.SimpleArbitrage;

public class DataForAnalyzer {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd HH:mm:ss");
	private static Database database = new Database();
	private static String[] codes = ArbitrageCodes.getCodes();
	
	public static List<SimpleArbitrage> getDataToday(int index){
		String code = codes[index];
		List<SimpleArbitrage> result = new ArrayList<SimpleArbitrage>();
		
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet =null;
		String today = DATE_FORMAT.format(new Date()).substring(0,8);
		String sql = "select LastPrice,TradingDay,UpdateTime from "+code+" where TradingDay="+today;
		connection = database.getConnection();
		try {
			preparedStatement =connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				double price = resultSet.getDouble(1);
				String day = resultSet.getString(2);
				String time = resultSet.getString(3);
				Date date = DATE_FORMAT.parse(day+" "+time);
				SimpleArbitrage tem = new SimpleArbitrage(price,date);
				result.add(tem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("没有找到记录");
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("格式转换有误");
		}
		database.terminate(resultSet,preparedStatement,connection);
		
		return result;
	}
	
	public static SimpleArbitrage getCurData(int index){
		String code = codes[index];
		SimpleArbitrage simpleArb = null;
		
		simpleArb = LiveData.getInstance().getData(code);
		
		return simpleArb;
	}
}
