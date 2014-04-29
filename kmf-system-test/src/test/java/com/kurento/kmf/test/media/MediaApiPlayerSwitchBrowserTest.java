/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.kurento.kmf.test.media;

import org.junit.Assert;
import org.junit.Test;

import com.kurento.kmf.media.HttpGetEndpoint;
import com.kurento.kmf.media.MediaPipeline;
import com.kurento.kmf.media.PlayerEndpoint;
import com.kurento.kmf.test.base.MediaApiTest;
import com.kurento.kmf.test.client.Browser;
import com.kurento.kmf.test.client.BrowserClient;
import com.kurento.kmf.test.client.Client;

/**
 * Test of a HTTP Player switching videos.
 * 
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 4.2.3
 */
public class MediaApiPlayerSwitchBrowserTest extends MediaApiTest {

	@Test
	public void testPlayerSwitch() throws Exception {
		// Media Pipeline
		MediaPipeline mp = pipelineFactory.create();
		PlayerEndpoint playerRed = mp.newPlayerEndpoint(
				"http://ci.kurento.com/video/color/red.webm").build();
		PlayerEndpoint playerBlue = mp.newPlayerEndpoint(
				"http://ci.kurento.com/video/color/blue.webm").build();
		PlayerEndpoint playerYellow = mp.newPlayerEndpoint(
				"http://ci.kurento.com/video/color/yellow.webm").build();
		PlayerEndpoint playerGreen = mp.newPlayerEndpoint(
				"http://ci.kurento.com/video/color/green.webm").build();
		PlayerEndpoint playerGrey = mp.newPlayerEndpoint(
				"http://ci.kurento.com/video/color/grey.webm").build();
		HttpGetEndpoint httpEP = mp.newHttpGetEndpoint().terminateOnEOS()
				.build();

		// Test execution
		try (BrowserClient browser = new BrowserClient(getServerPort(),
				Browser.CHROME, Client.PLAYER)) {
			browser.setURL(httpEP.getUrl());

			// Red
			playerRed.connect(httpEP);
			playerRed.play();
			browser.subscribeEvents("playing", "ended");
			browser.start();
			Assert.assertTrue(browser.waitForEvent("playing"));
			Thread.sleep(2000);

			// Blue
			playerBlue.connect(httpEP);
			playerBlue.play();
			Thread.sleep(2000);

			// Yellow
			playerYellow.connect(httpEP);
			playerYellow.play();
			Thread.sleep(2000);

			// Green
			playerGreen.connect(httpEP);
			playerGreen.play();
			Thread.sleep(2000);

			// Grey
			playerGrey.connect(httpEP);
			playerGrey.play();
			Assert.assertTrue(browser.waitForEvent("ended"));

			Assert.assertTrue("Playback time must be at least 8 seconds",
					browser.getCurrentTime() >= 8);
		}
	}

}