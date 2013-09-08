package org.cl.contenu.rest;

import static org.cl.contenu.rest.IConstantesCodeErreur.ERREUR_FORMAT_DATE_NAISSANCE;
import static org.cl.contenu.rest.IConstantesCodeErreur.URLS_REQUEST_ABSENTE;
import static org.cl.contenu.rest.IConstantesCodeErreur.URL_ABSENTE;
import static org.cl.contenu.rest.IConstantesTest.BAD_FORMAT_DATE;
import static org.cl.contenu.rest.IConstantesTest.GOOD_DATE;
import static org.cl.contenu.rest.IConstantesTest.GOOGLE_URL;
import static org.cl.contenu.rest.IConstantesTest.URL_13_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_15_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_17_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_39_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_9_ANS;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cl.contenu.domain.Url;
import org.cl.contenu.domain.UrlsRequest;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/test-context.xml" })
@WebAppConfiguration
public class ControlResourceTest {

	private static String SLASH_URL = "/url";
	private static String SLASH_URL_SLASH_MULTI = "/url/multi";
	private static String SLASH_URL_SLASH_LATENCE = "/url/latence";

	private static String PARAM_URL = "url";
	private static String PARAM_DATE_NAISSANCE = "dateNaissance";
	private static String PARAM_MIN = "min";
	private static String PARAM_MAX = "max";

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private LatenceBean latenceBean;

	@Before
	public void setUp() {
		if (this.mockMvc == null) {
			this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		}
	}

	/* test sur une URL */

	@Test
	public void doitObtenir400SiMauvaisFormatDateNaissance() throws Exception {
		mockMvc.perform(
				get(SLASH_URL).param(PARAM_URL, GOOGLE_URL).param(PARAM_DATE_NAISSANCE, BAD_FORMAT_DATE)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(ERREUR_FORMAT_DATE_NAISSANCE));
	}

	@Test
	public void doitObtenir400SiPasDUrl() throws Exception {
		mockMvc.perform(get(SLASH_URL).param(PARAM_DATE_NAISSANCE, GOOD_DATE).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenir400SiUrlVide() throws Exception {
		mockMvc.perform(
				get(SLASH_URL).param(PARAM_URL, "").param(PARAM_DATE_NAISSANCE, GOOD_DATE)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenir400SiUrlBlanc() throws Exception {
		mockMvc.perform(
				get(SLASH_URL).param(PARAM_URL, "     ").param(PARAM_DATE_NAISSANCE, GOOD_DATE)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenirOk() throws Exception {
		mockMvc.perform(
				get(SLASH_URL).param(PARAM_URL, GOOGLE_URL).param(PARAM_DATE_NAISSANCE, GOOD_DATE)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void doitValiderUrlPourAge() throws Exception {
		doTest(URL_9_ANS, 9, 11, false);
		doTest(URL_9_ANS, 9, 13, true);
		doTest(URL_13_ANS, 13, 11, false);
		doTest(URL_13_ANS, 13, 15, true);
		doTest(URL_15_ANS, 15, 14, false);
		doTest(URL_15_ANS, 15, 17, true);
		doTest(URL_17_ANS, 17, 17, false);
		doTest(URL_17_ANS, 17, 19, true);
		doTest(URL_39_ANS, 39, 17, false);
		doTest(URL_39_ANS, 39, 22, true);

	}

	private void doTest(String url, int scoreAttenduUrl, int age, boolean mustPass) throws Exception {

		DateTime datenaissance = DateTime.now().minusYears(age);

		int scoreUrl = Math.abs(url.hashCode() % 100);

		assertThat(scoreUrl).isEqualTo(scoreAttenduUrl);

		if (mustPass) {
			mockMvc.perform(
					get(SLASH_URL).param(PARAM_URL, url)
							.param(PARAM_DATE_NAISSANCE, datenaissance.toString("dd-MM-yyyy"))
							.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		} else {
			mockMvc.perform(
					get(SLASH_URL).param(PARAM_URL, url)
							.param(PARAM_DATE_NAISSANCE, datenaissance.toString("dd-MM-yyyy"))
							.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
		}
	}

	/* test sur plusieurs URL */

	private String getJSON(UrlsRequest urlsRequest) throws JsonGenerationException, JsonMappingException, IOException {
		return objectMapper.writeValueAsString(urlsRequest);
	}

	@Test
	public void doitObtenir400SiPasDeContenu() throws Exception {

		String content = getJSON(null);

		mockMvc.perform(
				post(SLASH_URL_SLASH_MULTI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isBadRequest())
				.andExpect(status().reason(URLS_REQUEST_ABSENTE));
	}

	private Date getDatenaissance4Age(int age) {
		return DateTime.now().minusYears(age).toDate();
	}

	@Test
	public void doitObtenir400SiPasDURLs() throws Exception {

		UrlsRequest urlsRequest = new UrlsRequest();
		urlsRequest.setUrls(null);
		urlsRequest.setDateNaissance(getDatenaissance4Age(25));

		String content = getJSON(urlsRequest);

		mockMvc.perform(
				post(SLASH_URL_SLASH_MULTI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isBadRequest())
				.andExpect(status().reason(URLS_REQUEST_ABSENTE));
	}

	@Test
	public void doitObtenir400SiPasDateNaissance() throws Exception {

		UrlsRequest urlsRequest = new UrlsRequest();
		urlsRequest.getUrls().add(new Url(GOOGLE_URL));
		urlsRequest.setDateNaissance(null);

		String content = getJSON(urlsRequest);

		mockMvc.perform(
				post(SLASH_URL_SLASH_MULTI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isBadRequest())
				.andExpect(status().reason(ERREUR_FORMAT_DATE_NAISSANCE));
	}

	private void doTest4Multi(int age, List<String> urlsAttendues) throws Exception {

		Date dateNaissance = getDatenaissance4Age(age);

		UrlsRequest urlsRequest = new UrlsRequest();
		urlsRequest.setDateNaissance(dateNaissance);

		urlsRequest.getUrls().add(new Url(URL_13_ANS));
		urlsRequest.getUrls().add(new Url(URL_15_ANS));
		urlsRequest.getUrls().add(new Url(URL_39_ANS));
		urlsRequest.getUrls().add(new Url(URL_17_ANS));
		urlsRequest.getUrls().add(new Url(URL_9_ANS));

		String content = getJSON(urlsRequest);

		// print().handle(
		mockMvc.perform(
				post(SLASH_URL_SLASH_MULTI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isOk())
				.andExpect(jsonPath("$..url", equalTo(urlsAttendues)));
		// .andReturn());

	}

	@Test
	public void doitObtenirUrlsMulti() throws Exception {

		List<String> urls;

		urls = new ArrayList<String>();
		doTest4Multi(5, urls);

		urls = Lists.newArrayList(URL_9_ANS);
		doTest4Multi(13, urls);

		urls = Lists.newArrayList(URL_13_ANS, URL_9_ANS);
		doTest4Multi(15, urls);

		urls = Lists.newArrayList(URL_13_ANS, URL_15_ANS, URL_9_ANS);
		doTest4Multi(17, urls);

		urls = Lists.newArrayList(URL_13_ANS, URL_15_ANS, URL_39_ANS, URL_17_ANS, URL_9_ANS);
		doTest4Multi(19, urls);

	}

	@Test
	@DirtiesContext
	public void doitModifierLatence() throws Exception {

		assertThat(latenceBean.getMin()).isEqualTo(0);
		assertThat(latenceBean.getMax()).isEqualTo(0);

		mockMvc.perform(
				post(SLASH_URL_SLASH_LATENCE).param(PARAM_MIN, "5").param(PARAM_MAX, "12")
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(equalTo("true")));

		assertThat(latenceBean.getMin()).isEqualTo(5);
		assertThat(latenceBean.getMax()).isEqualTo(12);

	}

}
