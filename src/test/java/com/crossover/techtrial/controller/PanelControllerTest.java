package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * @author Crossover
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PanelControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PanelController panelController;
  
  @Autowired
  private TestRestTemplate template;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(panelController).build();
  }

  @Test
  public void testPanelShouldBeRegistered() throws Exception {
    HttpEntity<Object> panel = getHttpEntity(
        "{\"serial\": \"1234567890123456\", \"longitude\": \"54.123232\","
            + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
    ResponseEntity<Panel> response = template.postForEntity(
        "/api/register", panel, Panel.class);
    Assert.assertEquals(202,response.getStatusCode().value());
  }

    @Test
    public void testPanelShouldRespondWithErrorForTooLongSerial() throws Exception {
        HttpEntity<Object> panel = getHttpEntity(
                "{\"serial\": \"1234567890123456789\", \"longitude\": \"54.123232\","
                        + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
        ResponseEntity<Panel> response = template.postForEntity(
                "/api/register", panel, Panel.class);
        Assert.assertEquals(400,response.getStatusCode().value());
    }

    @Test
    public void testPanelShouldRespondWithErrorForTooShortSerial() throws Exception {
        HttpEntity<Object> panel = getHttpEntity(
                "{\"serial\": \"12345678901234\", \"longitude\": \"54.123232\","
                        + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
        ResponseEntity<Panel> response = template.postForEntity(
                "/api/register", panel, Panel.class);
        Assert.assertEquals(400,response.getStatusCode().value());
    }

  @Test
  public void testHourlyElectricityShouldBeSaved() throws Exception {
    HttpEntity<Object> panel = getHttpEntity(
            "{\"panel\": \"26\", \"generatedElectricity\": \"120\","
                    + " \"readingAt\": \"2018-09-03T19:25:12.345\"}");
    ResponseEntity<Panel> response = template.postForEntity(
            "/api/panels/" + "1234567890123456" + "/hourly", panel, Panel.class);
    Assert.assertEquals(200,response.getStatusCode().value());
  }

    @Test
    public void testHourlyElectricityShouldBeReturnedforPanel() throws Exception {
        ResponseEntity<HourlyElectricity> response = template.getForEntity(
                "/api/panels/" + "1234567890123456" + "/hourly", HourlyElectricity.class);
        Assert.assertEquals(200,response.getStatusCode().value());
    }

    @Test
    public void testHourlyElectricityShouldRespondErrorForInvalidPanelSerial() throws Exception {
        ResponseEntity<HourlyElectricity> response = template.getForEntity(
                "/api/panels/" + "9999999999999999" + "/hourly", HourlyElectricity.class);
        Assert.assertEquals(404,response.getStatusCode().value());
    }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }
}
