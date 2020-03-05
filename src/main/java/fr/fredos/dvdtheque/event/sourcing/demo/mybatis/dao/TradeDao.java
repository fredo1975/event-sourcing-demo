package fr.fredos.dvdtheque.event.sourcing.demo.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeDbObject;

@Component
public class TradeDao {
	private final SqlSession sqlSession;
	public TradeDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public void save(TradeDbObject trade) {
		this.sqlSession.insert("save", trade);
	}
	
	public List<TradeDbObject> loadByAggregateId(final String aggregateId){
		return this.sqlSession.selectList("loadByAggregateId", aggregateId);
	}
	
	public List<TradeDbObject> loadAllNotSentEvents(){
		return this.sqlSession.selectList("loadAllNotSentEvents");
	}
	@ResultMap("BaseResultMap")
	public List<TradeDbObject> loadAllEvents(){
		return this.sqlSession.selectList("loadAllEvents");
	}
}
