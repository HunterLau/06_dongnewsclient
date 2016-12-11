package nouelleclient.ithema.com.a06_dongnewsclient;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nouelleclient.ithema.com.a06_dongnewsclient.domain.News;

public class MainActivity extends AppCompatActivity {
    List<News> newsList;
    Handler handler =new Handler(){
        public void handleMessage(android.os.Message msg){
            ListView lv = (ListView) findViewById(R.id.lv);
            lv.setAdapter(new MyAdapter());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getNewsInfo();
  /*      ListView lv = (ListView) findViewById(R.id.lv);  // 这两句是上移来的
        lv.setAdapter(new MyAdapter());
    */

    }

    class MyAdapter extends BaseAdapter{
        //返回的要显示的条目的数量
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return newsList.size();
        }
        //返回一个View对象，会作为ListView的一个条目显示在界面上
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            News news = newsList.get(position);
     /*       //convertView是缓存
            View v = null;
            if(convertView == null) {
                v = View.inflate(MainActivity.this, R.layout.item_listview, null);
            }else{v = convertView;}

                //View v = View.inflate(MainActivity.this, R.layout.item_listview, null);
            //给条目中每个组件设置要显示的内容
                TextView tv_title = (TextView) v.findViewById(R.id.tv_title);  //一定要 V.***
                tv_title.setText(news.getTitle());
                TextView tv_detail = (TextView) v.findViewById(R.id.tv_detail);
                tv_detail.setText(news.getDetail());
                TextView tv_comment = (TextView) v.findViewById(R.id.tv_comment);
                tv_comment.setText(news.getComment() + "条评论");
                //处理图片
                SmartImageView siv = (SmartImageView) v.findViewById(R.id.siv);
                siv.setImageUrl(news.getImageUrl());
*/
            View v = null;
            ViewHolder mHolder = null;
            if(convertView == null){
                v = View.inflate(MainActivity.this, R.layout.item_listview, null);

                //创建viewHoler封装所有条目使用的组件
                mHolder = new ViewHolder();

                mHolder.tv_title = (TextView) v.findViewById(R.id.tv_title);
                mHolder.tv_detail = (TextView) v.findViewById(R.id.tv_detail);
                mHolder.tv_comment = (TextView) v.findViewById(R.id.tv_comment);
                mHolder.siv = (SmartImageView) v.findViewById(R.id.siv);

                //把viewHolder封装至view对象中，这样view被缓存时，viewHolder也就被缓存了
                v.setTag(mHolder);
            }
            else{
                v = convertView;
                //从view中取出保存的viewHolder，viewHolder中就有所有的组件对象，不需要再去findViewById
                mHolder = (ViewHolder) v.getTag();
            }
            //给条目中的每个组件设置要显示的内容
            mHolder.tv_title.setText(news.getTitle());
            mHolder.tv_detail.setText(news.getDetail());
            mHolder.tv_comment.setText(news.getComment() + "条评论");

            mHolder.siv.setImageUrl(news.getImageUrl());
            return v;
        }

        //把条目需要使用到的所有组件封装在这个类中
        class ViewHolder{
            TextView tv_title;
            TextView tv_detail;
            TextView tv_comment;
            SmartImageView siv;
        }
        @Override
        public Object getItem(int position) {
            return newsList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private void getNewsInfo() {
        Thread t = new Thread() {
            @Override
            public void run() {
                String path = "http://192.168.2.105:8080/news.xml";
                try {
                    URL url = new URL(path);  //用路径构造连接对象
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //获取连接对象
                    conn.setRequestMethod("GET");//设置  字符型
                    conn.setConnectTimeout(8000);  //设置连接超时
                    conn.setReadTimeout(8000);
                    if (conn.getResponseCode() == 200) {  //请求码发出，200表示成功
                        //流里的信息是一个xml文件的文本信息，用xml解析器去解析，而不要作为文本去解析
                        InputStream is = conn.getInputStream(); //服务器发输入流给我们
                        getNewsFromStream(is);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };  //为什么 此处加分好？？？？
        t.start();
    }
    private void getNewsFromStream(InputStream is){
        XmlPullParser xp = Xml.newPullParser();
        try{
            xp.setInput(is,"utf-8");
            //获取事件类型，通过事件类型判断出当前解析的是什么节点
            int type = xp.getEventType();
            News news = null;
            while (type != XmlPullParser.END_DOCUMENT){ //没解析完执行
                switch (type){
                    case XmlPullParser.START_TAG:
                        if ("newslist".equals(xp.getName())) { //xp.getName为获取的news.xml当前节点名字
                           newsList = new ArrayList<News>();
                           }else if ("news".equals(xp.getName())){
                            news = new News();
                            }else if ("title".equals(xp.getName())){
                            String title = xp.nextText();
                            news.setTitle(title);
                            }else if("detail".equals(xp.getName())){
                            String detail = xp.nextText();
                            news.setDetail(detail);
                            } else if("comment".equals(xp.getName())){
                            String comment = xp.nextText();
                            news.setComment(comment);
                            }else if("image".equals(xp.getName())){
                            String image = xp.nextText();
                            news.setImageUrl(image);
                            }
                        break;
                    case XmlPullParser.END_TAG:
                        if("news".equals(xp.getName())){
                            newsList.add(news);
                        }
                        break;
                }
                //指针移动到下一个节点并返回事件类型
                type = xp.next();
            }
            //发送消息，让主线程刷新listview ,发空消息 不需要携带数据
            handler.sendEmptyMessage(1);
//			for (News n : newsList) {
//				System.out.println(n.toString());
//			}
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
