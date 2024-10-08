package ch.epfl.biop.ij2command;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;


public class EMCCD_Simulator extends DetectorSimulator {
	private boolean knownCamera=false;
	
	public double gainfactor=1000;
	
	public EMCCD_Simulator(double pixelSize, int exposureTime, double quantumEff, ImageProcessor ip){
		super(pixelSize,exposureTime,quantumEff/2, ip);
		
	}
	public EMCCD_Simulator(double gain){
		super(0.46,0.001,0.0018,0.105*gain,10);
		this.gainfactor=gain;
	}	
	public EMCCD_Simulator(ImagePlus imp, double gain){
		super(imp,0.46,0.001,0.0018,0.105*gain,10);
		this.gainfactor=gain;
	}
	public EMCCD_Simulator(ImagePlus imp, double quant, double dark, double cic, double read, double exp, double gain){
		super(imp,quant/2,dark,cic,read*gain,exp);
		this.gainfactor=gain;
	}
	
	public EMCCD_Simulator(ImagePlus imp, CameraDialog cd){
		super(imp,cd.quant/2,cd.dark,cd.cic,cd.read*cd.emGain,cd.exp);
		this.gainfactor=cd.emGain;
	}
	
	public EMCCD_Simulator(ImageProcessor ip, double quantumEff,Calibration cal){
			super(ip,quantumEff,cal);
	}
	public EMCCD_Simulator() {
		// TODO Auto-generated constructor stub
	}

	public EMCCD_Simulator(ImagePlus multiThreadCalculate, EMCCD_Simulator emccd) {
		
	}

	public ImagePlus run(){
		
		super.setTitle("EM CCD Camera");
		
		this.setImageProcessor(this.PoissonNoise());
		this.setImageProcessor(this.Bimodal());
		this.setImageProcessor(this.EMGain(gainfactor));
		this.setImageProcessor(this.ReadNoise());
		if (cam_imp==null) cam_imp=new ImagePlus(this.getTitle(),this.cam_ip);
		else this.cam_imp.setProcessor(this.cam_ip);
		
		return this.getImagePlus();
		
	}
	public ImagePlus run(ImagePlus imp) {
//		imp.show();
		int slice=imp.getStackSize();
		ImageStack stack=imp.createEmptyStack();
			
		for (int i=1;i<=slice;i++){
			IJ.showProgress(i/slice);
				
			imp.setSliceWithoutUpdate(i);
			this.setImageProcessor(imp.getProcessor());

			this.run();
			stack.addSlice(this.getProcessor());
			this.reset();

		}
		this.setImagePlus(stack);
		return this.getImagePlus();
	}
	
	ImagePlus resultStack(){
		 
		 ImageStack stack=new ImageStack(super.getWidth(),super.getHeight());
		 stack.addSlice(this.getImagePlus().getProcessor().duplicate().convertToFloat());
		 stack.addSlice(this.PoissonNoise().duplicate().convertToFloat());
		 stack.addSlice(this.Bimodal().duplicate());
		 
		 return new ImagePlus("Noise stack",stack);
	 }
	
}
