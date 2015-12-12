package edu.umich.eecs589.myapplication1;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by willwjs on 12/11/15.
 */
public class ClientSocket implements Runnable {
    private String ip;
    private int port;
    private Socket socket = null;
    OutputStream out = null;
    InputStream in = null;

    public ClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 创建socket连接
     *
     * @throws Exception
     *             exception
     */
    public void CreateConnection() {
        try {
            Log.e("out", ip + " " + port);
            socket = new Socket(ip, port);
            Log.e("out", "连接服务器成功!");
            in = socket.getInputStream();
            out = socket.getOutputStream();
            new Thread(this).start();
        } catch (Exception e) {
            Log.e("out", "连接服务器失败!");
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) throws Exception {
        writeString(out, msg);
        Log.e("out", msg);
    }

    public static void writeString(OutputStream out, String msg)
            throws Exception {
        byte[] b = msg.getBytes();
        Log.e("abc", "len = " + b.length);
        writeInt(out, b.length);
        out.write(b);
    }

    public static void writeInt(OutputStream out, int it) throws Exception {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; ++i) {
            b[i] = (byte) it;
            it >>= 8;
        }
        out.write(b);
    }

    public void shutDownConnection() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int readInt(InputStream in) throws Exception {
        byte[] b = new byte[4];
        int len = in.read(b);
        Log.e("abc", "read len = " + len);
        int i = 0;
        for (int j = 3; j >= 0; --j) {
            i = i << 8;
            i += b[j] & 0xff;
        }
        return i;
    }

    public static String readString(InputStream in) throws Exception {
        int t = readInt(in);
        byte[] b = new byte[t];
        in.read(b);
        return new String(b);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            // 不断监听输入流的数据情况
            Log.e("abc", "came in run");
            while (true) {
                // 当流中有数据时,读取数据并进行处理

                Log.e("abc", "read");
                // 创建data数组并将流中数据读取到数组中
                // Log.e("abc","mes "+in.readUTF());
                int t = readInt(in);
                Log.e("abc", "datasize=" + t);
                byte[] data = new byte[t];// 注此处同样没有处理图片大小超过int的范围的情况
                Log.e("abc", "new over");
                int readNum = 0;
                while (readNum < t) {
                    int size = in.read(data, readNum, t - readNum);
                    readNum += size;
                }
                Log.e("abc", "size = " + readNum);
                // 根据读到的文件数据创建Bitmap对象bitmap，因为将要在后面的内部类中使用bitmap，所以定义为
                /*final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                // 将图片显示到ImageView上
                // 此处由于view中的组件都是线程不安全的,使用android提供的这个办法处理（详见下文“附2”）
                image.post(new Runnable() {
                    public void run() {
                        // 将bitmap显示到界面上
                        image.setImageBitmap(bitmap);
                    }
                });*/
                // 线程休息1s
//				Thread.sleep(1000);
            }
        } catch (Exception e) {
            Log.e("abc", "exit");
            e.printStackTrace();
        }
    }
}