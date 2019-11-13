package eu.fasten.core.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static net.javacrumbs.jsonunit.JsonAssert.*;

import java.net.URISyntaxException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import eu.fasten.core.data.RevisionCallGraph.Constraint;
import eu.fasten.core.data.RevisionCallGraph.Dependency;
import it.unimi.dsi.fastutil.objects.ObjectLists;

class RevisionCallGraphTest {

	@Test
	void testConstraint() {
		RevisionCallGraph.Constraint c;
		
		c = new RevisionCallGraph.Constraint("3.1", "7.4");
		assertEquals("3.1", c.lowerBound);
		assertEquals("7.4", c.upperBound);
		
		c = new RevisionCallGraph.Constraint("[3.1..7.4]");
		assertEquals("3.1", c.lowerBound);
		assertEquals("7.4", c.upperBound);
		
		c = new RevisionCallGraph.Constraint("[3.1..]");
		assertEquals("3.1", c.lowerBound);
		assertNull(c.upperBound);
		
		c = new RevisionCallGraph.Constraint("[   ..  3.1]");
		assertNull(c.lowerBound);
		assertEquals("3.1", c.upperBound);
		assertEquals("[..3.1]", c.toString());

		
		c = new RevisionCallGraph.Constraint("[3.1]");
		assertEquals("3.1", c.lowerBound);
		assertEquals("3.1", c.upperBound);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new RevisionCallGraph.Constraint("joooo");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new RevisionCallGraph.Constraint("[a..b..c]");
		});
		
		String spec = "[\"[3.1..  7.1   ]\",\"[   9]\",\"[10.3  ..]\"]";
		JSONArray cs = new JSONArray(spec);
		List<Constraint> constraints = RevisionCallGraph.Constraint.constraints(cs);
		assertEquals(3, constraints.size());
		assertEquals("3.1", constraints.get(0).lowerBound);
		assertEquals("7.1", constraints.get(0).upperBound);
		assertEquals("9", constraints.get(1).lowerBound);
		assertEquals("9", constraints.get(1).upperBound);
		assertEquals("10.3", constraints.get(2).lowerBound);
		assertNull(constraints.get(2).upperBound);
		
		assertEquals(new JSONArray(spec.replaceAll(" ", "")).toString(), RevisionCallGraph.Constraint.toJSON(constraints).toString());
	}
	
	@Test
	void testDependency() {
		RevisionCallGraph.Dependency d;
		d = new RevisionCallGraph.Dependency("maven", "foo.bar", ObjectLists.singleton(new Constraint("[3.1..7.1]")));
		assertEquals("maven", d.forge);
		assertEquals("foo.bar", d.product);
		assertEquals(1, d.constraints.size());
		assertEquals("3.1", d.constraints.get(0).lowerBound);
		assertEquals("7.1", d.constraints.get(0).upperBound);
		
		String spec = "{\"forge\": \"maven\", \"product\": \"foo.bar\", \"constraints\": [\"[3.1..  7.1   ]\",\"[   9]\",\"[10.3  ..]\"] }";
		JSONObject json = new JSONObject(spec);
		d = new RevisionCallGraph.Dependency(json, false);
		assertEquals("maven", d.forge);
		assertEquals("foo.bar", d.product);
		List<Constraint> constraints = d.constraints;
		assertEquals(3, constraints.size());
		assertEquals("3.1", constraints.get(0).lowerBound);
		assertEquals("7.1", constraints.get(0).upperBound);
		assertEquals("9", constraints.get(1).lowerBound);
		assertEquals("9", constraints.get(1).upperBound);
		assertEquals("10.3", constraints.get(2).lowerBound);
		assertNull(constraints.get(2).upperBound);
		
		assertJsonEquals(new JSONObject(spec.replaceAll(" ", "")), d.toJSON());
	}
	
	@Test
	void testDepset() {
		String spec = "[" +
				"[{\"forge\": \"maven\", \"product\": \"foo.bar\", \"constraints\": [\"[3.1..  7.1   ]\",\"[   9]\",\"[10.3  ..]\"] }], " +
				"[{\"forge\": \"other\", \"product\": \"bar.nee\", \"constraints\": [\"[..9]\",\"[10.3  ..]\"] }]" +
				"]";
		JSONArray json = new JSONArray(spec);
		List<List<Dependency>> depset = RevisionCallGraph.Dependency.depset(json, false);
		Dependency d = depset.get(0).get(0);
		assertEquals("maven", d.forge);
		assertEquals("foo.bar", d.product);
		List<Constraint> constraints = d.constraints;
		assertEquals(3, constraints.size());
		assertEquals("3.1", constraints.get(0).lowerBound);
		assertEquals("7.1", constraints.get(0).upperBound);
		assertEquals("9", constraints.get(1).lowerBound);
		assertEquals("9", constraints.get(1).upperBound);
		assertEquals("10.3", constraints.get(2).lowerBound);
		assertNull(constraints.get(2).upperBound);
		d = depset.get(1).get(0);
		assertEquals("other", d.forge);
		assertEquals("bar.nee", d.product);
		constraints = d.constraints;
		assertEquals(2, constraints.size());
		assertNull(constraints.get(0).lowerBound);
		assertEquals("9", constraints.get(0).upperBound);
		assertEquals("10.3", constraints.get(1).lowerBound);
		assertNull(constraints.get(1).upperBound);
		
		assertJsonEquals(new JSONArray(spec.replaceAll(" ", "")), Dependency.toJSON(depset));
	}

	@Test
	void testCallGraph() throws JSONException, URISyntaxException {
		String callGraph = "{\n" + 
				"    \"forge\": \"mvn\",\n" + 
				"    \"product\": \"foo\",\n" + 
				"    \"version\": \"2.0\",\n" + 
				"    \"depset\":\n" + 
				"      [\n" + 
				"        [{ \"forge\": \"mvn\", \"product\": \"a\", \"constraints\": [\"[1.0..2.0]\", \"[4.2..]\"]}],\n" + 
				"        [{ \"forge\": \"other\", \"product\": \"b\", \"constraints\": [\"[4.3.2]\"]}]\n" + 
				"      ],\n" + 
				"    \"graph\": \n" + 
				"      [\n" + 
				"        [\"/my.package/A.f(A)B\",\n" + 
				"         \"/my.other.package/C.g(%2Fmy.package%2FA)%2Fmy.package%2FB\"],\n" + 
				"        [\"/my.package/A.g(A,%2F%2Fjdk%2Fjava.lang%2Fint)%2F%2Fjdk%2Fjava.lang%2Fint\",\n" + 
				"         \"//b/their.package/TheirClass.method(TheirOtherClass)TheirOtherClass\"],\n" + 
				"      ]\n" + 
				"}";
		JSONObject json = new JSONObject(callGraph);
		RevisionCallGraph cg = new RevisionCallGraph(json, false);
		assertEquals("mvn", cg.forge);
		assertEquals("foo", cg.product);
		assertEquals("2.0", cg.version);
		List<List<Dependency>> depset = cg.depset;
		assertEquals(2, depset.size());
		assertEquals("mvn", depset.get(0).get(0).forge);
		assertEquals("a", depset.get(0).get(0).product);
		List<Constraint> constraints = depset.get(0).get(0).constraints;
		assertEquals(2, constraints.size());
		assertEquals("1.0", constraints.get(0).lowerBound);
		assertEquals("2.0", constraints.get(0).upperBound);
		assertEquals("4.2", constraints.get(1).lowerBound);
		assertNull(constraints.get(1).upperBound);
		assertEquals("mvn", depset.get(0).get(0).forge);
		assertEquals("a", depset.get(0).get(0).product);
		constraints = depset.get(1).get(0).constraints;
		assertEquals(1, constraints.size());
		assertEquals("4.3.2", constraints.get(0).lowerBound);
		assertEquals("4.3.2", constraints.get(0).upperBound);
		
		assertJsonEquals(new JSONObject(callGraph.replace(" ", "")), cg.toJSON());
	}

}
