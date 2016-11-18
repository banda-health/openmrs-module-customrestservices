package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by andrew on 11/4/16.
 */
public class PatientListTemplate {

	public static void main(String[] args) throws Exception {
		System.out.println("test templating");
		test();
	}

	public static void test() throws Exception {

		String temp = "<span class='tmp_header'><b>[[p.first_name]]</b> - <i>[[p.gender]]</i></span>";
		temp += "<span class='tmp_body'><p>[[v.display]]</p><p>[[v.attr.bed]] - [[v.attr.ward]]</p></span>";

		// Set parameters for my template.
		VelocityContext context = new VelocityContext();
		StringWriter sw = new StringWriter();
		StringReader sr = new StringReader(temp);
		context.put("$p.first_name", "andrew");
		Velocity.evaluate(context, sw, PatientListTemplate.class.getName(), sr);

		System.out.println("anything?? " + sw.toString());

		String[] subs = StringUtils.substringsBetween(temp, "[[", "]]");

		for (String s : subs) {
			System.out.println(s);
		}

		// Show the result.
		//System.out.println(writer.toString());
	}
}
