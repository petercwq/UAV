namespace RTKServer
{
    partial class ServerForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
        	this.components = new System.ComponentModel.Container();
        	this.tb_Msg = new System.Windows.Forms.TextBox();
        	this.lb_InClient = new System.Windows.Forms.ListBox();
        	this.lb_OutClient = new System.Windows.Forms.ListBox();
        	this.mtb_InPort = new System.Windows.Forms.MaskedTextBox();
        	this.mtb_OutPort = new System.Windows.Forms.MaskedTextBox();
        	this.bt_Start = new System.Windows.Forms.Button();
        	this.bt_Stop = new System.Windows.Forms.Button();
        	this.label1 = new System.Windows.Forms.Label();
        	this.label2 = new System.Windows.Forms.Label();
        	this.label3 = new System.Windows.Forms.Label();
        	this.label4 = new System.Windows.Forms.Label();
        	this.label5 = new System.Windows.Forms.Label();
        	this.errorProvider = new System.Windows.Forms.ErrorProvider(this.components);
        	((System.ComponentModel.ISupportInitialize)(this.errorProvider)).BeginInit();
        	this.SuspendLayout();
        	// 
        	// tb_Msg
        	// 
        	this.tb_Msg.Location = new System.Drawing.Point(31, 268);
        	this.tb_Msg.Multiline = true;
        	this.tb_Msg.Name = "tb_Msg";
        	this.tb_Msg.Size = new System.Drawing.Size(463, 109);
        	this.tb_Msg.TabIndex = 3;
        	// 
        	// lb_InClient
        	// 
        	this.lb_InClient.FormattingEnabled = true;
        	this.lb_InClient.ItemHeight = 12;
        	this.lb_InClient.Location = new System.Drawing.Point(30, 121);
        	this.lb_InClient.Name = "lb_InClient";
        	this.lb_InClient.Size = new System.Drawing.Size(215, 124);
        	this.lb_InClient.TabIndex = 4;
        	// 
        	// lb_OutClient
        	// 
        	this.lb_OutClient.FormattingEnabled = true;
        	this.lb_OutClient.ItemHeight = 12;
        	this.lb_OutClient.Location = new System.Drawing.Point(279, 121);
        	this.lb_OutClient.Name = "lb_OutClient";
        	this.lb_OutClient.Size = new System.Drawing.Size(215, 124);
        	this.lb_OutClient.TabIndex = 5;
        	// 
        	// mtb_InPort
        	// 
        	this.mtb_InPort.Location = new System.Drawing.Point(108, 33);
        	this.mtb_InPort.Mask = "99999";
        	this.mtb_InPort.Name = "mtb_InPort";
        	this.mtb_InPort.PromptChar = ' ';
        	this.mtb_InPort.Size = new System.Drawing.Size(63, 21);
        	this.mtb_InPort.TabIndex = 6;
        	this.mtb_InPort.Text = "2013";
        	this.mtb_InPort.Validating += new System.ComponentModel.CancelEventHandler(this.mtb_Port_Validating);
        	// 
        	// mtb_OutPort
        	// 
        	this.mtb_OutPort.Location = new System.Drawing.Point(108, 60);
        	this.mtb_OutPort.Mask = "99999";
        	this.mtb_OutPort.Name = "mtb_OutPort";
        	this.mtb_OutPort.PromptChar = ' ';
        	this.mtb_OutPort.Size = new System.Drawing.Size(63, 21);
        	this.mtb_OutPort.TabIndex = 7;
        	this.mtb_OutPort.Text = "2014";
        	this.mtb_OutPort.Validating += new System.ComponentModel.CancelEventHandler(this.mtb_Port_Validating);
        	// 
        	// bt_Start
        	// 
        	this.bt_Start.Location = new System.Drawing.Point(278, 33);
        	this.bt_Start.Name = "bt_Start";
        	this.bt_Start.Size = new System.Drawing.Size(75, 48);
        	this.bt_Start.TabIndex = 8;
        	this.bt_Start.Text = "Start";
        	this.bt_Start.UseVisualStyleBackColor = true;
        	this.bt_Start.Click += new System.EventHandler(this.bt_Start_Click);
        	// 
        	// bt_Stop
        	// 
        	this.bt_Stop.Location = new System.Drawing.Point(384, 33);
        	this.bt_Stop.Name = "bt_Stop";
        	this.bt_Stop.Size = new System.Drawing.Size(75, 48);
        	this.bt_Stop.TabIndex = 9;
        	this.bt_Stop.Text = "Stop";
        	this.bt_Stop.UseVisualStyleBackColor = true;
        	this.bt_Stop.Click += new System.EventHandler(this.bt_Stop_Click);
        	// 
        	// label1
        	// 
        	this.label1.AutoSize = true;
        	this.label1.Location = new System.Drawing.Point(31, 37);
        	this.label1.Name = "label1";
        	this.label1.Size = new System.Drawing.Size(71, 12);
        	this.label1.TabIndex = 10;
        	this.label1.Text = "Base Port :";
        	// 
        	// label2
        	// 
        	this.label2.AutoSize = true;
        	this.label2.Location = new System.Drawing.Point(31, 64);
        	this.label2.Name = "label2";
        	this.label2.Size = new System.Drawing.Size(77, 12);
        	this.label2.TabIndex = 11;
        	this.label2.Text = "Rover Port :";
        	// 
        	// label3
        	// 
        	this.label3.AutoSize = true;
        	this.label3.Location = new System.Drawing.Point(30, 106);
        	this.label3.Name = "label3";
        	this.label3.Size = new System.Drawing.Size(89, 12);
        	this.label3.TabIndex = 12;
        	this.label3.Text = "Base Clients :";
        	// 
        	// label4
        	// 
        	this.label4.AutoSize = true;
        	this.label4.Location = new System.Drawing.Point(279, 106);
        	this.label4.Name = "label4";
        	this.label4.Size = new System.Drawing.Size(95, 12);
        	this.label4.TabIndex = 13;
        	this.label4.Text = "Rover Clients :";
        	// 
        	// label5
        	// 
        	this.label5.AutoSize = true;
        	this.label5.Location = new System.Drawing.Point(31, 253);
        	this.label5.Name = "label5";
        	this.label5.Size = new System.Drawing.Size(35, 12);
        	this.label5.TabIndex = 14;
        	this.label5.Text = "Log :";
        	// 
        	// errorProvider
        	// 
        	this.errorProvider.ContainerControl = this;
        	// 
        	// ServerForm
        	// 
        	this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
        	this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
        	this.ClientSize = new System.Drawing.Size(514, 398);
        	this.Controls.Add(this.label5);
        	this.Controls.Add(this.label4);
        	this.Controls.Add(this.label3);
        	this.Controls.Add(this.label2);
        	this.Controls.Add(this.label1);
        	this.Controls.Add(this.bt_Stop);
        	this.Controls.Add(this.bt_Start);
        	this.Controls.Add(this.mtb_OutPort);
        	this.Controls.Add(this.mtb_InPort);
        	this.Controls.Add(this.lb_OutClient);
        	this.Controls.Add(this.lb_InClient);
        	this.Controls.Add(this.tb_Msg);
        	this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
        	this.MaximizeBox = false;
        	this.Name = "ServerForm";
        	this.Text = "RTKServer";
        	((System.ComponentModel.ISupportInitialize)(this.errorProvider)).EndInit();
        	this.ResumeLayout(false);
        	this.PerformLayout();
        }

        #endregion

        private System.Windows.Forms.TextBox tb_Msg;
        private System.Windows.Forms.ListBox lb_InClient;
        private System.Windows.Forms.ListBox lb_OutClient;
        private System.Windows.Forms.MaskedTextBox mtb_InPort;
        private System.Windows.Forms.MaskedTextBox mtb_OutPort;
        private System.Windows.Forms.Button bt_Start;
        private System.Windows.Forms.Button bt_Stop;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.ErrorProvider errorProvider;
    }
}

