/******************************************************************************
 *
 * Name:	ServerForm
 * Project:	RTKServer
 * Purpose:	
 * Description:	
 * Version:	1.0
 * Created:	4/8/2013 5:31:30 PM
 * Compiler:	Visual Studio 2008/2010
 *
 * Author:	Peter Chen, peter_chen@trimble.com
 * Company:	Trimble Navigation Ltd.
 *
 ******************************************************************************
 *
 * Copyright (c) 2013, Peter Chen
 *
 *****************************************************************************/


using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Net;
using System.Net.Sockets;
using System.Windows.Forms;

namespace RTKServer
{
    internal static class ESocket
    {
        public static string ToIpString(this Socket s)
        {
            IPEndPoint ep = s.RemoteEndPoint as IPEndPoint;
            return ep.Address + ":" + ep.Port.ToString();
        }
    }

    public partial class ServerForm : Form
    {
        delegate void AppendText(string s);
        delegate void Void();

        List<Socket> clientListIn, clientListOut;
        Socket serverSocketIn, serverSocketOut;
        byte[] byteData = new byte[4096];
        bool stoped = true;

        Timer timer;

        public ServerForm()
        {
            clientListIn = new List<Socket>();
            clientListOut = new List<Socket>();
            InitializeComponent();
            bt_Start.Enabled = true;
            bt_Stop.Enabled = false;

            timer = new Timer();
            timer.Interval = 1000 * 10;
            timer.Tick += new EventHandler(timer_Tick);
            //timer.Enabled = true;
        }

        void timer_Tick(object sender, EventArgs e)
        {
            for (int i = 0; i < clientListIn.Count; i++)
            {
                Socket s = clientListIn[i];
                bool remove = false;
                if (!s.Connected)
                {
                    remove = true;
                }
                if (s.Poll(-1, SelectMode.SelectWrite))
                {
                }
                else if (s.Poll(-1, SelectMode.SelectRead))
                {
                }
                else if (s.Poll(-1, SelectMode.SelectError))
                {
                    remove = true;
                }
                else
                {
                }

                if (remove)
                {
                    clientListIn.Remove(s);
                    s.Close();
                    i--;
                }
            }

            for (int i = 0; i < clientListOut.Count; i++)
            {
                Socket s = clientListOut[i];
                bool remove = false;
                if (!s.Connected)
                {
                    remove = true;
                }
                if (s.Poll(-1, SelectMode.SelectWrite))
                {
                }
                else if (s.Poll(-1, SelectMode.SelectRead))
                {
                }
                else if (s.Poll(-1, SelectMode.SelectError))
                {
                    remove = true;
                }
                else
                {
                }

                if (remove)
                {
                    clientListOut.Remove(s);
                    s.Close();
                    i--;
                }
            }

            lb_InClient.Invoke(new Void(RefreshIn));
            lb_OutClient.Invoke(new Void(RefreshOut));
        }

        private void OnAcceptIn(IAsyncResult ar)
        {
            if (stoped)
                return;
            try
            {
                Socket clientSocket = serverSocketIn.EndAccept(ar);
                serverSocketIn.BeginAccept(new AsyncCallback(OnAcceptIn), null);
                clientSocket.BeginReceive(byteData, 0, byteData.Length, SocketFlags.None, new AsyncCallback(OnReceiveIn), clientSocket);
                clientListIn.Add(clientSocket);
                lb_InClient.Invoke(new Void(RefreshIn));
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void OnAcceptOut(IAsyncResult ar)
        {
            if (stoped)
                return;
            try
            {
                Socket clientSocket = serverSocketOut.EndAccept(ar);
                serverSocketOut.BeginAccept(new AsyncCallback(OnAcceptOut), null);
                //clientSocket.BeginReceive(byteData, 0, byteData.Length, SocketFlags.None, new AsyncCallback(OnReceiveOut), clientSocket);
                clientListOut.Add(clientSocket);
                lb_OutClient.Invoke(new Void(RefreshOut));
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void OnReceiveIn(IAsyncResult ar)
        {
            if (stoped)
                return;
            try
            {
                Socket clientSocket = (Socket)ar.AsyncState;
                int read = clientSocket.EndReceive(ar);
                if (read > 0)
                {
                    log(BytesToHexStr(byteData, 0, read) + Environment.NewLine);

                    int i = 0;
                    while (i < clientListOut.Count)
                    {
                        bool isValid = true;
                        if (clientListOut[i].Connected)
                        {
                            try
                            {
                                clientListOut[i].BeginSend(byteData, 0, read, SocketFlags.None, new AsyncCallback(OnSend), clientListOut[i]);
                            }
                            catch
                            {
                                isValid = false;
                            }
                        }
                        else
                        {
                            isValid = false;
                        }

                        if (isValid)
                            i++;
                        else
                        {
                            clientListOut[i].Shutdown(SocketShutdown.Both);
                            clientListOut.RemoveAt(i);
                            lb_OutClient.Invoke(new Void(RefreshOut));
                        }
                    }
                }

                clientSocket.BeginReceive(byteData, 0, byteData.Length, SocketFlags.None, new AsyncCallback(OnReceiveIn), clientSocket);
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void OnReceiveOut(IAsyncResult ar)
        {
            if (stoped)
                return;
            try
            {
                Socket clientSocket = (Socket)ar.AsyncState;
                clientSocket.EndReceive(ar);
                clientSocket.BeginReceive(byteData, 0, byteData.Length, SocketFlags.None, new AsyncCallback(OnReceiveOut), clientSocket);
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        public void OnSend(IAsyncResult ar)
        {
            try
            {
                Socket client = (Socket)ar.AsyncState;
                client.EndSend(ar);
                if (!clientListOut.Contains(client))
                    client.Close();
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void bt_Start_Click(object sender, EventArgs e)
        {
            if (!isValidPorts)
                return;
            try
            {
                serverSocketIn = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                serverSocketOut = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                IPEndPoint ipEndPointIn = new IPEndPoint(IPAddress.Any, int.Parse(mtb_InPort.Text));
                IPEndPoint ipEndPointOut = new IPEndPoint(IPAddress.Any, int.Parse(mtb_OutPort.Text));
                serverSocketIn.Bind(ipEndPointIn);
                serverSocketOut.Bind(ipEndPointOut);
                serverSocketIn.Listen(4);
                serverSocketOut.Listen(4);
                serverSocketIn.BeginAccept(new AsyncCallback(OnAcceptIn), null);
                serverSocketOut.BeginAccept(new AsyncCallback(OnAcceptOut), null);

                bt_Start.Enabled = false;
                bt_Stop.Enabled = true;

                stoped = false;
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void bt_Stop_Click(object sender, EventArgs e)
        {
            bt_Stop.Enabled = false;
            bt_Start.Enabled = true;
            try
            {
                foreach (Socket s in clientListOut)
                {
                    s.Shutdown(SocketShutdown.Both);
                    s.Close();
                }
                clientListOut.Clear();
                lb_OutClient.Invoke(new Void(RefreshOut));
                foreach (Socket s in clientListIn)
                {
                    s.Shutdown(SocketShutdown.Both);
                    s.Close();
                }
                clientListIn.Clear();
                lb_InClient.Invoke(new Void(RefreshIn));

                ReleaseSocket(serverSocketIn);
                ReleaseSocket(serverSocketOut);
                stoped = true;
            }
            catch (Exception ex)
            {
                log(ex.Message);
            }
        }

        private void ReleaseSocket(Socket cliSock)
        {
            if (cliSock != null)
            {
                if (cliSock.Connected)
                {
                    cliSock.Shutdown(SocketShutdown.Both);
                    System.Threading.Thread.Sleep(10);
                }
                cliSock.Close();
            }
        }

        private void mtb_Port_Validating(object sender, CancelEventArgs e)
        {
            string text = (sender as Control).Text;
            if (!string.IsNullOrEmpty(text) && int.Parse(text) > 1023 && int.Parse(text) < 65536)
                errorProvider.SetError((Control)sender, string.Empty);
            else
                errorProvider.SetError((Control)sender, "Port must be 1024~65535");
        }

        private bool isValidPorts
        {
            get
            {
                return !string.IsNullOrEmpty(mtb_InPort.Text) && int.Parse(mtb_InPort.Text) > 1023 && int.Parse(mtb_InPort.Text) < 65536
                    && !string.IsNullOrEmpty(mtb_OutPort.Text) && int.Parse(mtb_OutPort.Text) > 1023 && int.Parse(mtb_OutPort.Text) < 65536;
            }
        }

        private void RefreshIn()
        {
            lb_InClient.Items.Clear();
            foreach (Socket s in clientListIn)
                lb_InClient.Items.Add(s.ToIpString());
        }

        private void RefreshOut()
        {
            lb_OutClient.Items.Clear();
            foreach (Socket s in clientListOut)
                lb_OutClient.Items.Add(s.ToIpString());
        }

        private void log(string s)
        {
            if (tb_Msg.InvokeRequired)
            {
                tb_Msg.Invoke(new AppendText(tb_Msg.AppendText), s);
            }
            else
                tb_Msg.AppendText(s);
        }

        public static string BytesToHexStr(byte[] bytes, int start, int length)
        {
            string returnStr = "";
            if (bytes != null)
            {
                for (int i = start; i < start + length && i < bytes.Length; i++)
                {
                    returnStr += bytes[i].ToString("X2") + " ";
                }
            }
            return returnStr;
        }
    }
}
