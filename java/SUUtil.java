import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

/*
  Author: Gorav Gupta
 */

class SUUtil {

    static boolean isSuAvailable() {
        File file = new File("/data", ".modify24x7");
        runSU("mkdir " + file.getAbsolutePath());
        if (file.exists()) {
            runSU("rm " + file.getAbsolutePath());
            return true;
        } else {
            return false;
        }
    }

    // Output: No
    static void runSU(String command) {
        try {
            runSU(command, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Output: First Line
    static String runSU_OutputFirstLine(String command) {
        try {
            return runSU(command, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Output: Full
    static String runSU_Output(String command) {
        try {
            return runSU(command, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Output: Full, error
    static String runSU_OutputError(String command) {
        try {
            return runSU(command, 3);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /***
     *
     * @param command
     * @param output := 0 = Full, 1 = Single Line, 2 = No Output, 3 = Full + Error
     * @return output string
     * @throws Exception
     */
    private static String runSU(String command, int output) throws Exception {
        Process process = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuilder log = new StringBuilder();

            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(command + "\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();

            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            if (output == 0 || output == 3) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(line).append("\n");
                }

                if (output == 3) {
                    log.append("\n");
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    while ((line = bufferedReader.readLine()) != null) {
                        log.append(line).append("\n");
                    }
                }

            } else if (output == 1) {
                log.append(bufferedReader.readLine());
            } else if (output == 2) {
                log.append("");
            }

            process.waitFor();

            if (process.exitValue() == 0) {
                bufferedReader.close();
                dataOutputStream.close();
                process.destroy();
                return log.toString();
            } else {
                bufferedReader.close();
                dataOutputStream.close();
                process.destroy();
				if(output == 3) {
                    return log.toString();
				} else {
					return "";
				}
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedReader != null) {
                dataOutputStream.close();
            }
            if (bufferedReader != null) {
                process.destroy();
            }
        }
    }
}
