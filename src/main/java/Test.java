import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {

    public static void main(String[] args) throws Exception {

        String url = "http://10.238.10.91:8000/api/microservice/trades/mobcomteresale/v1";
        //url = "http://127.0.0.1:40001/servlet/testtaobaoServlet";

        URL restURL = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // 请求方式
        conn.setRequestMethod("POST");
        // 设置是否从httpUrlConnection读入，默认情况下是true; httpUrlConnection.setDoInput(true);
        conn.setDoOutput(true);
        // allowUserInteraction 如果为 true，则在允许用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查。
        conn.setAllowUserInteraction(false);

        String params = "{\"UNI_BSS_HEAD\":{\"TRANS_ID\":\"20201116110155767309567\",\"TIMESTAMP\":\"2020-11-16 11:01:55.767\",\"APP_ID\":\"GDDDXTTEST\",\"TOKEN\":\"58537cacd7c5a405cc618babba830e2d\"},\"UNI_BSS_BODY\":{\"AOP_MOBCOMTE_RESALE_REQ\":{\"APPKEY\":\"cmall.sub\",\"MSG\":{\"channelId\":\"51b0mpo\",\"channelType\":\"1030100\",\"city\":\"538\",\"cost\":\"97500\",\"dbFlag\":\"1\",\"discountPrices\":\"0\",\"district\":\"51a7be\",\"feeInfo\":[{\"feeCategory\":\"6\",\"feeDes\":\"终端费用\",\"feeId\":\"96\",\"origFee\":\"119000\",\"realFee\":\"59000\",\"reliefFee\":\"60000\",\"reliefResult\":\"无\"}],\"machineTypeCode\":\"7430106297\",\"netType\":\"00\",\"oldOrdersId\":\"5120110773734034\",\"opeSysType\":\"2\",\"operatorId\":\"TJYFJ025\",\"ordersId\":\"2011070855592092\",\"payInfo\":[{\"barCode\":\"\",\"bonPwd\":\"\",\"mblNo\":\"\",\"payFee\":\"119000\",\"payNum\":\"\",\"payOrg\":\"\",\"payType\":\"10\"}],\"province\":\"51\",\"resourcesCode\":\"360MR42023627208\",\"salePrices\":\"119000\",\"subsysCode\":\"\",\"terminalType\":\"00\"},\"APPTX\":\"20201116110155Y2etCv\"}}}";
        PrintStream ps = new PrintStream(conn.getOutputStream());
        ps.print(params);
        ps.close();

        BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder resultBuilder = new StringBuilder();
        for(String line=null; (line = bReader.readLine()) != null;) {
            resultBuilder.append(line).append("\r\n");
        }

        System.out.println(resultBuilder);
        bReader.close();

    }


}
