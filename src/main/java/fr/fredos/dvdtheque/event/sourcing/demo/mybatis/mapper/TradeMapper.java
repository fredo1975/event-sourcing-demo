package fr.fredos.dvdtheque.event.sourcing.demo.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeDbObject;

@Mapper
public interface TradeMapper {
	void save(TradeDbObject tradeEntity);

	List<TradeDbObject> loadByAggregateId(String aggregateId);

	List<TradeDbObject> loadAllNotSentEvents();

	List<TradeDbObject> loadAllEvents();
}
