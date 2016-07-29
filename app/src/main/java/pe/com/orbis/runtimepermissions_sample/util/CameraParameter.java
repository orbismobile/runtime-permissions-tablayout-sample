package pe.com.orbis.runtimepermissions_sample.util;

import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Carlos Vargas on 13/05/16.
 * Alias: CarlitosDroid
 */
public class CameraParameter {
	private static final String tag = "yan";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CameraParameter cameraParameter = null;
	private CameraParameter(){
		
	}
	public static CameraParameter getInstance(){
		if(cameraParameter == null){
			cameraParameter = new CameraParameter();
			return cameraParameter;
		}
		else{
			return cameraParameter;
		}
	}

	public Size getPictureSize(List<Size> list, int th){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.width > th) && equalRate(s, 1.33f)){
				Log.i(tag, "The final set the picture size: W = " + s.width + "h = " + s.height);
                break;
			}
			i++;
		}

		return list.get(i);
	}
	
	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.2)
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public  class CameraSizeComparator implements Comparator<Size> {
		//In ascending order
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
			return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}
		
	}
}