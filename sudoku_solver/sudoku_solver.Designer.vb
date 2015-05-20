<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Class Form1
    Inherits System.Windows.Forms.Form

    'Form overrides dispose to clean up the component list.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        Try
            If disposing AndAlso components IsNot Nothing Then
                components.Dispose()
            End If
        Finally
            MyBase.Dispose(disposing)
        End Try
    End Sub

    'Required by the Windows Form Designer
    Private components As System.ComponentModel.IContainer

    'NOTE: The following procedure is required by the Windows Form Designer
    'It can be modified using the Windows Form Designer.  
    'Do not modify it using the code editor.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Me.SolveButton = New System.Windows.Forms.Button()
        Me.ClearButton = New System.Windows.Forms.Button()
        Me.SuspendLayout()
        '
        'SolveButton
        '
        Me.SolveButton.Location = New System.Drawing.Point(210, 226)
        Me.SolveButton.Name = "SolveButton"
        Me.SolveButton.Size = New System.Drawing.Size(75, 23)
        Me.SolveButton.TabIndex = 0
        Me.SolveButton.Text = "Solve"
        Me.SolveButton.UseVisualStyleBackColor = True
        '
        'ClearButton
        '
        Me.ClearButton.Location = New System.Drawing.Point(49, 226)
        Me.ClearButton.Name = "ClearButton"
        Me.ClearButton.Size = New System.Drawing.Size(75, 23)
        Me.ClearButton.TabIndex = 1
        Me.ClearButton.Text = "Clear"
        Me.ClearButton.UseVisualStyleBackColor = True
        '
        'Form1
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(6.0!, 13.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font
        Me.ClientSize = New System.Drawing.Size(293, 261)
        Me.Controls.Add(Me.ClearButton)
        Me.Controls.Add(Me.SolveButton)
        Me.Name = "Form1"
        Me.Text = "SudokuSolver"
        Me.ResumeLayout(False)

    End Sub
    Friend WithEvents SolveButton As System.Windows.Forms.Button
    Friend WithEvents ClearButton As System.Windows.Forms.Button

End Class
