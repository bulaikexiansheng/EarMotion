package com.example.android.PythonExecutor;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class PythonExecutor {
    //  数据规模
    private final int DATASIZE = 20 ;
    //  通道数
    private final int CHANNALS = 3 ;
    private Python python ;
    private final static PythonExecutor  executor = new PythonExecutor() ;
    private PythonExecutor() {
        python = Python.getInstance() ;
    }

    public static PythonExecutor getExecutor() {
        return executor;
    }
    private float[][] initData(String []gx,String []gy,String []gz){
        // 合成数据
        float[][] conbinedArray = new float[DATASIZE][CHANNALS];
        for (int i = 0; i < CHANNALS; i++) {
            for (int j = 0; j < DATASIZE; j++) {
                switch (i) {
                    case 0:
                        conbinedArray[j][i] = Float.parseFloat(gx[j]);
                        break;
                    case 1:
                        conbinedArray[j][i] = Float.parseFloat(gy[j]);
                        break;
                    case 2:
                        conbinedArray[j][i] = Float.parseFloat(gz[j]);
                        break;
                }
            }
        }
        return conbinedArray ;
    }
    // 主执行代码，首先对数据进行初始化，调用模型
    public int execute(String []gx,String []gy,String []gz){
        float [][]conbinedArray = initData(gx,gy,gz) ;
        // 将Java二维数组转换成Python NDArray数据
        PyObject np = python.getModule("numpy");
        PyObject conbined_final = np.callAttr("array", conbinedArray[0]);
        conbined_final = np.callAttr("expand_dims", conbined_final, 0);
        for (int i = 1; i < conbinedArray.length; i++) {
            PyObject temp_arr = np.callAttr("expand_dims", conbinedArray[i], 0);
            conbined_final = np.callAttr("append", conbined_final, temp_arr, 0);
        }
        PyObject obj1 = python.getModule("Deployment").callAttr("predict", conbined_final);
        int result = obj1.toJava(int.class);
        return result ;
    }
}
