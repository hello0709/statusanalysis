package com.tbea.statusanalysis.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MQTTClient extends JFrame implements ActionListener{

    /**
     *
     */
    private static final long serialVersionUID = 560476157988482663L;
    private JPanel contentPane;
    private Font useFont = new Font("微软雅黑", Font.BOLD, 12);
    private JFormattedTextField frmserverIP;
    private JFormattedTextField username;
    private JButton button_connect;
    private JButton button_disconnect;
    private JFormattedTextField toptic_input;
    private JFormattedTextField toptic_titile;
    private  JLabel err_infoText;
    private JComboBox<String> comboBox;
    private JButton sendbutton;
    private JTextArea msgtextArea;
    private String brokerIP;
    private String clientId;
    private  String msgContent;
    private String sub_topic,pub_topic;
    private int qos = 0;
    private  MqttClient sampleClient;
    private  String infoList="";
    private 	JButton subbutton;



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MQTTClient frame = new MQTTClient();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MQTTClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 484, 419);
        setTitle("MQTT客户端");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblMqtt = new JLabel("MQTT服务器地址:");
        lblMqtt.setFont(useFont);
        lblMqtt.setBounds(30, 10, 109, 15);
        contentPane.add(lblMqtt);

        frmserverIP = new JFormattedTextField();
        frmserverIP.setText("tcp://192.168.3.151:1883");
        frmserverIP.setBounds(139, 7, 258, 21);
        contentPane.add(frmserverIP);

        JLabel lblNewLabel = new JLabel("用户名:");
        lblNewLabel.setFont(useFont);
        lblNewLabel.setBounds(40, 41, 54, 15);
        contentPane.add(lblNewLabel);

        username= new JFormattedTextField();
        username.setText("chenbo");
        username.setBounds(139, 38, 99, 21);
        contentPane.add(username);

        button_connect= new JButton("连  接");
        button_connect.setBounds(30, 66, 93, 23);
        button_connect.setFont(useFont);
        button_connect.addActionListener(this);
        button_connect.setActionCommand("connect");
        contentPane.add(button_connect);

        button_disconnect= new JButton("断  开");
        button_disconnect.setEnabled(false);
        button_disconnect.setFont(useFont);
        button_disconnect.addActionListener(this);
        button_disconnect.setActionCommand("disconnect");
        button_disconnect.setBounds(263, 66, 93, 23);
        contentPane.add(button_disconnect);

        JLabel label = new JLabel("订阅主题:");
        label.setBounds(40, 111, 83, 15);
        label.setFont(useFont);
        contentPane.add(label);

        toptic_input= new JFormattedTextField();
        toptic_input.setText("info");
        toptic_input.setBounds(139, 108, 99, 21);
        contentPane.add(toptic_input);

        JSeparator separator = new JSeparator();
        separator.setBounds(30, 99, 367, 2);
        contentPane.add(separator);

        subbutton = new JButton("订  阅");
        subbutton.setFont(useFont);
        subbutton.addActionListener(this);
        subbutton.setActionCommand("sub");
        subbutton.setBounds(263, 107, 93, 23);
        contentPane.add(subbutton);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(30, 139, 367, 2);
        contentPane.add(separator_1);

        JLabel label_1 = new JLabel("发布消息");
        label_1.setFont(useFont);
        label_1.setBounds(40, 151, 60, 15);
        contentPane.add(label_1);

        JLabel label_2 = new JLabel("主题");
        label_2.setFont(useFont);
        label_2.setBounds(104, 151, 30, 15);
        contentPane.add(label_2);

        toptic_titile= new JFormattedTextField();
        toptic_titile.setText("test");
        toptic_titile.setBounds(139, 148, 99, 21);
        contentPane.add(toptic_titile);

        JLabel label_3 = new JLabel("服务质量");
        label_3.setFont(useFont);
        label_3.setBounds(248, 151, 54, 15);
        contentPane.add(label_3);

        comboBox= new JComboBox<String>();
        comboBox.setBounds(305, 148, 51, 21);
        comboBox.addItem("0");
        comboBox.addItem("1");
        comboBox.addItem("2");
        contentPane.add(comboBox);

        sendbutton= new JButton("发  送");
        sendbutton.setBounds(46, 338, 93, 23);
        sendbutton.addActionListener(this);
        sendbutton.setActionCommand("send");
        contentPane.add(sendbutton);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(40, 179, 316, 149);
        contentPane.add(scrollPane);

        msgtextArea = new JTextArea();
        scrollPane.setViewportView(msgtextArea);

        err_infoText = new JLabel("");
        err_infoText.setForeground(Color.RED);
        err_infoText.setBounds(134, 69, 131, 15);
        err_infoText.setFont(useFont);
        contentPane.add(err_infoText);

        JButton btnClear = new JButton("clear");
        btnClear.setBounds(209, 338, 93, 23);
        btnClear.addActionListener(this);
        btnClear.setActionCommand("clear");
        contentPane.add(btnClear);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("connect")){

            MemoryPersistence persistence = new MemoryPersistence();

            try {
                brokerIP = frmserverIP.getText();
                clientId = username.getText();
                if(TextUtils.isEmpty(brokerIP)){
                    err_infoText.setText("输入服务器地址");
                    return;
                }
                if(TextUtils.isEmpty(clientId)){
                    err_infoText.setText("输入ID");
                    return;
                }
                sampleClient= new MqttClient(brokerIP, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                System.out.println("Connecting to broker: "+brokerIP);
                sampleClient.connect(connOpts);
                System.out.println("Connected");
                button_disconnect.setEnabled(true);
                button_connect.setEnabled(false);

                sampleClient.setCallback(new MqttCallback() {

                    @Override
                    public void messageArrived(String title, MqttMessage msg) throws Exception {
                        System.out.println("收到消息:"+title);
                        String info = msg.toString();
                        infoList += info+"\n";
                        msgtextArea.setText(infoList);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken arg0) {
                        try {
                            System.out.println(arg0.getMessage());
                        } catch (MqttException e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void connectionLost(Throwable err) {
                        err_infoText.setText("连接丢失");
                        System.out.println("连接丢失");
                        System.out.println(err.getMessage());

                    }
                });

            } catch(MqttException me) {
                System.out.println("reason "+me.getReasonCode());
                System.out.println("msg "+me.getMessage());
                System.out.println("loc "+me.getLocalizedMessage());
                System.out.println("cause "+me.getCause());
                System.out.println("excep "+me);
                err_infoText.setText(me.getMessage());
                button_disconnect.setEnabled(false);
                button_connect.setEnabled(true);
                me.printStackTrace();
            }

        }else if (command.equals("sub")){
            sub_topic = toptic_input.getText();
            if(TextUtils.isEmpty(sub_topic)){
                err_infoText.setText("输入订阅主题");
                return;
            }
            try {
                sampleClient.subscribe(sub_topic);
                subbutton.setEnabled(false);
            } catch (MqttException e1) {
                e1.printStackTrace();
            }

        }else if(command.equals("send")){
            pub_topic = toptic_titile.getText();
            msgContent = msgtextArea.getText();
            if(TextUtils.isEmpty(pub_topic)){
                err_infoText.setText("输入发送主题");
                return;
            }
            if(TextUtils.isEmpty(msgContent)){
                err_infoText.setText("输入消息内容");
                return;
            }
            MqttMessage message = new MqttMessage(msgContent.getBytes());
            String qoset = comboBox.getSelectedItem().toString();
            if(qoset=="0"){
                qos =0;
            }else if(qoset=="1"){
                qos = 1;
            }else{
                qos = 2;
            }
            message.setQos(qos);
            try {
                sampleClient.publish(pub_topic, message);
            } catch (MqttException e1) {
                e1.printStackTrace();
                err_infoText.setText(e1.getMessage());
            }

        }else if(command.equals("clear")){
            msgtextArea.setText("");
            infoList = "";


        }
        else if(command.equals("disconnect")){
            if(sampleClient!=null){
                try {
                    sampleClient.disconnect();
                    button_connect.setEnabled(true);
                    button_disconnect.setEnabled(false);
                } catch (MqttException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
