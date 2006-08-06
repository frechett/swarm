package gov.usgs.swarm.wave;

import gov.usgs.math.Butterworth.FilterType;
import gov.usgs.swarm.Swarm;
import gov.usgs.swarm.SwarmDialog;
import gov.usgs.swarm.wave.WaveViewSettings.ViewType;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/05 18:06:49  dcervelli
 * Major 1.3 changes.
 *
 * @author Dan Cervelli
 */
public class WaveViewSettingsDialog extends SwarmDialog
{
	private static final long serialVersionUID = 1L;

	private JPanel dialogPanel;
	
	private static WaveViewSettingsDialog dialog;
	private WaveViewSettings settings;
	
	private ButtonGroup viewGroup;
	private JRadioButton waveButton;
	private JCheckBox removeBias;
	private JRadioButton spectraButton;
	private JRadioButton spectrogramButton;
	
	private ButtonGroup waveScaleGroup;
	private JRadioButton waveAutoScale;
	private JRadioButton waveManualScale;
	private JCheckBox waveAutoScaleMemory;
	private JTextField minAmp;
	private JTextField maxAmp;
	
	private JCheckBox logPower;
	private JCheckBox logFreq;
	private JTextField maxPower;
	private JTextField minFreq;
	private JTextField maxFreq;
	private ButtonGroup spectraScaleGroup;
	private JRadioButton spectraAutoScale;
	private JRadioButton spectraManualScale;
	private JCheckBox spectraAutoScaleMemory;
	private JComboBox fftSize;
	private JSlider spectrogramOverlap;
	
	private ButtonGroup filterGroup;
	private JCheckBox filterEnabled;
	private JRadioButton lowPass;
	private JRadioButton highPass;
	private JRadioButton bandPass;
	private JCheckBox zeroPhaseShift;
	private JTextField corner1;
	private JTextField corner2;
	private JSlider order;
	
	private WaveViewSettingsDialog()
	{
		super(Swarm.getApplication(), "Wave Settings", true);
		createUI();
		setSizeAndLocation();
	}

	public static WaveViewSettingsDialog getInstance(WaveViewSettings s)
	{
		if (dialog == null)
			dialog = new WaveViewSettingsDialog();

		dialog.setSettings(s);
		dialog.setToCurrent();
		return dialog;
	}
	
	public void setSettings(WaveViewSettings s)
	{
	    settings = s;
	}
	
	public void setToCurrent()
	{
		switch (settings.viewType)
		{
			case WAVE:
				waveButton.setSelected(true);
				break;
			case SPECTRA:
				spectraButton.setSelected(true);
				break;
			case SPECTROGRAM:
				spectrogramButton.setSelected(true);
				break;
		}
		removeBias.setSelected(settings.removeBias);
		
		if (settings.autoScaleAmp)
			waveAutoScale.setSelected(true);
		else
			waveManualScale.setSelected(true);
		waveAutoScaleMemory.setSelected(settings.autoScaleAmpMemory);

		if (settings.autoScalePower)
			spectraAutoScale.setSelected(true);
		else
			spectraManualScale.setSelected(true);
		spectraAutoScaleMemory.setSelected(settings.autoScalePowerMemory);
		
		minAmp.setText(String.format("%.1f", settings.minAmp));
		maxAmp.setText(String.format("%.1f", settings.maxAmp));
		maxPower.setText(String.format("%.1f", settings.maxPower));

		fftSize.setSelectedItem(settings.fftSize);
		minFreq.setText(String.format("%.1f", settings.minFreq));
		maxFreq.setText(String.format("%.1f", settings.maxFreq));
		logFreq.setSelected(settings.logFreq);
		logPower.setSelected(settings.logPower);
		spectrogramOverlap.setValue((int)Math.round(settings.spectrogramOverlap * 100));
		filterEnabled.setSelected(settings.filterOn);
		
		switch (settings.filter.getType())
		{
			case LOWPASS:
				lowPass.setSelected(true);
				corner1.setText("0.0");
				corner2.setText(String.format("%.1f", settings.filter.getCorner1()));
				break;
			case HIGHPASS:
				highPass.setSelected(true);
				corner1.setText(String.format("%.1f", settings.filter.getCorner1()));
				corner2.setText("0.0");
				break;
			case BANDPASS:
				bandPass.setSelected(true);
				corner1.setText(String.format("%.1f", settings.filter.getCorner1()));
				corner2.setText(String.format("%.1f", settings.filter.getCorner2()));
				break;
		}
		order.setValue(settings.filter.getOrder());
	}
	
	private void createComponents()
	{
		viewGroup = new ButtonGroup();
		waveButton = new JRadioButton("Wave");
		spectraButton = new JRadioButton("Spectra");
		spectrogramButton = new JRadioButton("Spectrogram");
		viewGroup.add(waveButton);
		viewGroup.add(spectraButton);
		viewGroup.add(spectrogramButton);
		
		waveScaleGroup = new ButtonGroup();
		removeBias = new JCheckBox("Remove bias");
		waveAutoScale = new JRadioButton("Autoscale");
		waveManualScale = new JRadioButton("Manual scale");
		waveScaleGroup.add(waveAutoScale);
		waveScaleGroup.add(waveManualScale);
		waveAutoScaleMemory = new JCheckBox("Memory");
		minAmp = new JTextField(7);
		maxAmp = new JTextField(7);
		
		logPower = new JCheckBox("Log power");
		logFreq = new JCheckBox("Log frequency");
		maxPower = new JTextField(7);
		minFreq = new JTextField(7);
		maxFreq = new JTextField(7);
		spectraScaleGroup = new ButtonGroup();
		spectraAutoScale = new JRadioButton("Autoscale");
		spectraManualScale = new JRadioButton("Manual scale");
		spectraScaleGroup.add(spectraAutoScale);
		spectraScaleGroup.add(spectraManualScale);
		spectraAutoScaleMemory = new JCheckBox("Memory");
		fftSize = new JComboBox(new String[] {"Auto", "64", "128", "256", "512", "1024", "2048"});
		spectrogramOverlap = new JSlider(0, 90, 20);
		spectrogramOverlap.setMajorTickSpacing(15);
		spectrogramOverlap.setMinorTickSpacing(5);
		spectrogramOverlap.setSnapToTicks(true);
		spectrogramOverlap.createStandardLabels(15);
		spectrogramOverlap.setPaintLabels(true);
		
		filterGroup = new ButtonGroup();
		filterEnabled = new JCheckBox("Enabled");
		lowPass = new JRadioButton("Low pass");
		highPass = new JRadioButton("High pass");
		bandPass = new JRadioButton("Band pass");
		filterGroup.add(lowPass);
		filterGroup.add(highPass);
		filterGroup.add(bandPass);
		zeroPhaseShift = new JCheckBox("Zero phase shift (doubles order)");
		corner1 = new JTextField(7);
		corner2 = new JTextField(7);
		order = new JSlider(2, 8, 4);
		order.setMajorTickSpacing(2);
		order.setSnapToTicks(true);
		order.createStandardLabels(2);
		order.setPaintLabels(true);
	}
	
	protected void createUI()
	{
		super.createUI();
		createComponents();
		FormLayout layout = new FormLayout(
				"left:60dlu, 3dlu, left:60dlu, 3dlu, left:60dlu, 3dlu, left:60dlu", 
				"");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.appendSeparator("View");
		builder.nextLine();
		builder.append(waveButton);
		builder.append(spectraButton);
		builder.append(spectrogramButton);
		builder.nextLine();
		
		builder.appendSeparator("Wave Options");
		builder.nextLine();
		builder.append(removeBias, 3);
		builder.append(waveAutoScale);
		builder.append(waveAutoScaleMemory);
		builder.nextLine();
		builder.append(new JLabel(""), 3);
		builder.append(waveManualScale);
		builder.nextLine();
		builder.append(new JLabel(""), 3);
		builder.add(new JLabel("Minimum:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(minAmp);
		builder.nextLine();
		builder.append(new JLabel(""), 3);
		builder.add(new JLabel("Maximum:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(maxAmp);
		builder.nextLine();
		
		builder.appendSeparator("Spectra/Spectrogram Options");
		builder.nextLine();
		builder.append(logPower, 3);
		builder.append(spectraAutoScale);
		builder.append(spectraAutoScaleMemory);
		builder.nextLine();
		builder.append(logFreq, 3);
		builder.append(spectraManualScale);
		builder.nextLine();
		builder.appendRow("center:17dlu");
		builder.add(new JLabel("Min. frequency:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(minFreq);
		builder.add(new JLabel("Max. power:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(maxPower);
		builder.nextLine();
		builder.appendRow("center:17dlu");
		builder.add(new JLabel("Max. frequency:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(maxFreq);
		builder.add(new JLabel("Spectrogram overlap (%)"), cc.xyw(builder.getColumn(), builder.getRow(), 3, "center, center"));
		builder.nextLine();
		builder.appendRow("center:20dlu");
		builder.add(new JLabel("Spectrogram bin:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(fftSize);
		builder.append(spectrogramOverlap, 3);
		builder.nextLine();
		
		builder.appendSeparator("Butterworth Filter");
		builder.append(filterEnabled, 3);
		builder.append(zeroPhaseShift, 3);
		builder.nextLine();
		builder.append(lowPass, 3);
		builder.add(new JLabel("Min. frequency:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(corner1);
		builder.nextLine();
		builder.append(highPass, 3);
		builder.add(new JLabel("Max. frequency:"), cc.xy(builder.getColumn(), builder.getRow(), "right, center"));
		builder.nextColumn(2);
		builder.append(corner2);
		builder.nextLine();
		builder.append(bandPass, 3);
		builder.add(new JLabel("Order"), cc.xyw(builder.getColumn(), builder.getRow(), 3, "center, center"));
		builder.nextLine();
		builder.appendRow("center:20dlu");
		builder.nextColumn(3);
		builder.append(order, 4);
		builder.nextLine();
		
		dialogPanel = builder.getPanel();
		mainPanel.add(dialogPanel, BorderLayout.CENTER);
	}
	
	public boolean allowOK()
	{
		String message = null;
		try
		{
			message = "Error in minimum ampitude format.";
			double min = Double.parseDouble(minAmp.getText());
			message = "Error in maximum ampitude format.";
			double max = Double.parseDouble(maxAmp.getText());
			message = "Minimum amplitude must be less than maximum amplitude.";
			if (min >= max)
				throw new NumberFormatException();
				
			message = "Error in minimum frequency format.";
			double minf = Double.parseDouble(minFreq.getText());
			message = "Error in maximum frequency format.";
			double maxf = Double.parseDouble(maxFreq.getText());
			message = "Minimum frequency must be 0 or above and less than maximum frequency.";
			if (minf < 0 || minf >= maxf)
				throw new NumberFormatException();
			
			message = "Error in maximum power.";
			double maxp = Double.parseDouble(maxPower.getText());
			message = "Maximum power must be above 0.";
			if (maxp < 0)
				throw new NumberFormatException();
			
			message = "Error in spectrogram overlap format.";
			double so = spectrogramOverlap.getValue();
			message = "Spectrogram overlap must be between 0 and 95%.";
			if (so < 0 || so > 95)
				throw new NumberFormatException();
			
			message = "Error in minimum Hz format.";
			double c1 = Double.parseDouble(corner1.getText());
			message = "Minimum Hz must be 0 or above.";
			if (c1 < 0)
				throw new NumberFormatException();
			
			message = "Error in maximum Hz format.";
			double c2 = Double.parseDouble(corner2.getText());
			message = "Maximum Hz must be 0 or above.";
			if (c2 < 0)
				throw new NumberFormatException();
			
			message = "Minimum Hz must be less than maximum Hz.";
			if (bandPass.isSelected())
				if (c1 >= c2)
					throw new NumberFormatException();
			
			return true;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, message, "Options Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
	
	public void wasOK()
	{
		try
		{
			if (waveButton.isSelected())
				settings.viewType = ViewType.WAVE;
			else if (spectraButton.isSelected())
				settings.viewType = ViewType.SPECTRA;
			else if (spectrogramButton.isSelected())
				settings.viewType = ViewType.SPECTROGRAM;
				
			settings.removeBias = removeBias.isSelected();
			settings.autoScaleAmp = waveAutoScale.isSelected();
			settings.autoScaleAmpMemory = waveAutoScaleMemory.isSelected();
			
			settings.autoScalePower = spectraAutoScale.isSelected();
			settings.autoScalePowerMemory = spectraAutoScaleMemory.isSelected();
			settings.maxPower = Double.parseDouble(maxPower.getText());
			
			settings.minAmp = Double.parseDouble(minAmp.getText());
			settings.maxAmp = Double.parseDouble(maxAmp.getText());
			
			settings.maxFreq = Double.parseDouble(maxFreq.getText());
			settings.minFreq = Double.parseDouble(minFreq.getText());
			if (settings.minFreq < 0)
				settings.minFreq = 0;
			
			settings.fftSize = (String)fftSize.getSelectedItem();
			settings.logFreq = logFreq.isSelected();
			settings.logPower = logPower.isSelected();
			settings.spectrogramOverlap = spectrogramOverlap.getValue() / 100.0;
			if (settings.spectrogramOverlap > 0.95 || settings.spectrogramOverlap < 0)
				settings.spectrogramOverlap = 0;
			settings.notifyView();

			settings.filterOn = filterEnabled.isSelected();
			settings.zeroPhaseShift = zeroPhaseShift.isSelected();
			
			FilterType ft = null;
			double c1 = 0;
			double c2 = 0;
			if (lowPass.isSelected())
			{
				ft = FilterType.LOWPASS;
				c1 = Double.parseDouble(corner2.getText());
				c2 = 0;
			}
			else if (highPass.isSelected())
			{
				ft = FilterType.HIGHPASS;
				c1 = Double.parseDouble(corner1.getText());
				c2 = 0;
			}
			else if (bandPass.isSelected())
			{
				ft = FilterType.BANDPASS;
				c1 = Double.parseDouble(corner1.getText());
				c2 = Double.parseDouble(corner2.getText());
			}
			settings.filter.set(ft, order.getValue(), 100, c1, c2);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Illegal values.", "Options Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}