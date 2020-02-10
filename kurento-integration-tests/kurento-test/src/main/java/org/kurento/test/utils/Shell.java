/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kurento.test.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.kurento.commons.exception.KurentoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

/**
 * Local shell.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 4.2.3
 */
public class Shell {

  public static class ProcessResult {
    public String output;
    public int exitCode;

    public ProcessResult(String output, int exitCode) {
      this.output = output;
      this.exitCode = exitCode;
    }
  }

  public static Logger log = LoggerFactory.getLogger(Shell.class);

  public static Process run(final String... command) {
    return run(true, command);
  }

  public static Process run(final boolean redirectOutputs, final String... command) {
    log.debug("Run command: {}", Arrays.toString(command));

    try {
      final ProcessBuilder p = new ProcessBuilder(command);
      p.redirectErrorStream(true);
      if (redirectOutputs) {
        p.redirectOutput(ProcessBuilder.Redirect.INHERIT);
      }
      return p.start();
    } catch (final IOException e) {
      throw new RuntimeException(
          "Exception while executing command '" + Arrays.toString(command) + "'", e);
    }
  }

  public static String runAndWaitString(final String command) {
    return runAndWait(command.split(" "));
  }

  public static String runAndWait(final String... command) {
    return runAndWaitArray(command).output;
  }

  public static ProcessResult runAndWaitResult(final String... command) {
    return runAndWaitArray(command);
  }

  public static ProcessResult runAndWaitArray(final String[] command) {
    log.debug("Run and wait for command: {}", Arrays.toString(command));
    final ProcessResult result = runAndWaitNoLog(command);

    log.debug("Command output: '{}'", result.output);
    log.debug("Command exit code: {}", result.exitCode);

    return result;
  }

  public static ProcessResult runAndWaitNoLog(final String... command) {
    String output = "";
    int exitCode = 0;

    try {
      Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
      output = CharStreams.toString(new InputStreamReader(process.getInputStream(), "UTF-8"));
      exitCode = process.waitFor();
    } catch (final InterruptedException e) {
      output = "(interrupted)";
      exitCode = 1;
    } catch (final IOException e) {
      throw new KurentoException(
          "Exception executing command on the shell: " + Arrays.toString(command), e);
    }

    return new ProcessResult(output.trim(), exitCode);
  }

}
