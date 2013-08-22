package org.cl.contenu.rest;

import static org.cl.contenu.rest.IConstantesTest.BAD_FORMAT_DATE;
import static org.cl.contenu.rest.IConstantesTest.GOOGLE_URL;
import static org.cl.contenu.rest.IConstantesTest.GOOD_DATE;
import static org.cl.contenu.rest.IConstantesTest.URL_13_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_9_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_15_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_17_ANS;
import static org.cl.contenu.rest.IConstantesTest.URL_39_ANS;

import static org.cl.contenu.rest.IConstantesCodeErreur.ERREUR_FORMAT_DATE_NAISSANCE;
import static org.cl.contenu.rest.IConstantesCodeErreur.URL_ABSENTE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/test-context.xml" })
@WebAppConfiguration
public class ControlResourceTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	}

	@Test
	public void doitObtenir400SiMauvaisFormatDateNaissance() throws Exception {
		mockMvc.perform(
				post("/url").param("url", GOOGLE_URL).param("dateNaissance", BAD_FORMAT_DATE)
						.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(status().reason(ERREUR_FORMAT_DATE_NAISSANCE));
	}

	@Test
	public void doitObtenir400SiPasDUrl() throws Exception {
		mockMvc.perform(post("/url").param("dateNaissance", GOOD_DATE).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenir400SiUrlVide() throws Exception {
		mockMvc.perform(
				post("/url").param("url", "").param("dateNaissance", GOOD_DATE).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenir400SiUrlBlanc() throws Exception {
		mockMvc.perform(
				post("/url").param("url", "     ").param("dateNaissance", GOOD_DATE).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(status().reason(URL_ABSENTE));
	}

	@Test
	public void doitObtenirOk() throws Exception {
		mockMvc.perform(
				post("/url").param("url", GOOGLE_URL).param("dateNaissance", GOOD_DATE)
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
					post("/url").param("url", url).param("dateNaissance", datenaissance.toString("dd-MM-yyyy"))
							.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		} else {
			mockMvc.perform(
					post("/url").param("url", url).param("dateNaissance", datenaissance.toString("dd-MM-yyyy"))
							.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
		}
	}
}
