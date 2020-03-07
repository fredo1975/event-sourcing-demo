package fr.fredos.dvdtheque.event.sourcing.demo.trade.rest.service;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.TradeService;

@SpringBootTest(classes = { fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class })
@AutoConfigureMockMvc
@ActiveProfiles("mybatis")
public class EventControllerTest {
	protected Logger logger = LoggerFactory.getLogger(EventControllerTest.class);
	@Autowired
	private MockMvc mvc;
	@Autowired
	private TradeService tradeService;
	@Autowired
	private ObjectMapper mapper;
	private static final String LOAD_TRADE_BY_AGGREGATE_ID_URI = "/events/load/byAggregateId/";
	private static final String ENTER_CFIN_TRADE_BY_AGGREGATE_ID_URI = "/events/manualCfin/byAggregateId/";
	private static final String CFIN = "00000";
	
	@Test
	public void loadTrade() throws Exception {
		Trade trade = tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals("00000", trade.getCfin());
		assertEquals(Integer.valueOf(2),Integer.valueOf(trade.getBaseVersion()));
		String tradeAsString = mvc
				.perform(MockMvcRequestBuilders.get(LOAD_TRADE_BY_AGGREGATE_ID_URI + trade.getId()).param("aggregateId", trade.getId())
						.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(trade.getId())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.cfin", Is.is(CFIN)))
				.andReturn().getResponse().getContentAsString();
		assertTrue(StringUtils.isNotEmpty(tradeAsString));
	}
	
	@Test
	public void processTradeManualCfin() throws Exception {
		final String aggregateId = randomUUID().toString();
		tradeService.processCfinFailed(new TradeReceiveBookCommand(aggregateId, "FR0000", "EUR", 1000.0d, 50));
		String tradeAsString = mvc.perform(MockMvcRequestBuilders.put(ENTER_CFIN_TRADE_BY_AGGREGATE_ID_URI + aggregateId)
				.param("cfin", CFIN)
				.contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(aggregateId)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.cfin", Is.is(CFIN)))
				.andReturn().getResponse().getContentAsString();
		
		assertTrue(StringUtils.isNotEmpty(tradeAsString));
		
	}
}
