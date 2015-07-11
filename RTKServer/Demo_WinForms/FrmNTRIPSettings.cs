/******************************************************************************
 *
 * Name:	FrmNTRIPSettings
 * Project:	RTKServer
 * Purpose:	
 * Description:	
 * Version:	1.0
 * Created:	12/23/2013 1:31:30 PM
 * Compiler:	Visual Studio 2008/2010/2013
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
using System.ComponentModel;
using System.Windows.Forms;

namespace Demo_WinForms
{
    public partial class FrmNTRIPSettings : Form
    {
        string[] ports;
        string[] rates;

        public FrmNTRIPSettings()
        {
            InitializeComponent();
            ports = SharpGis.SharpGps.SerialPort.GetSerialPortNames(); //System.IO.Ports.SerialPort.GetPortNames();
            rates = new string[] { "9600", "57600", "115200" };
            cmbPorts.DataSource = ports;
            cmbBaudRates.DataSource = rates;
            cmbReCom.DataSource = ports;
        }

        public string[] COMs
        {
            get
            {
                string[] coms = new string[lbComs.Items.Count];
                for(int i=0;i<coms.Length;i++)
                {
                    coms[i] = lbComs.Items[i].ToString();
                }
                return coms;
            }
        }

        public string ReCom
        {
            get { return cmbReCom.Text; }
        }
        
        public void DisableConfig()
        {
            EnableDisable(false);
        }

        public void EnableConfig()
        {
            EnableDisable(true);
        }

        private void EnableDisable(bool enable)
        {
            cmbPorts.Enabled = enable;
            cmbBaudRates.Enabled = enable;
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            e.Cancel = true;
            base.OnClosing(e);
            this.Hide();
        }

        private void btAdd_Click(object sender, EventArgs e)
        {
            if (!String.IsNullOrEmpty(cmbPorts.Text) && !String.IsNullOrEmpty(cmbBaudRates.Text))
            {
                string newcom = cmbPorts.Text + "," + cmbBaudRates.Text;
                if (!lbComs.Items.Contains(newcom))
                    lbComs.Items.Add(newcom);
            }
        }

        private void btRemove_Click(object sender, EventArgs e)
        {
            if (lbComs.Items.Count > 0)
                lbComs.Items.RemoveAt(lbComs.Items.Count - 1);
        }
    }
}