package cm.seeds.rdtsmartreader.imagedecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.googlecode.tesseract.android.TessBaseAPI;

import static android.content.ContentValues.TAG;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(Context context, String language) {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        boolean fileExistFlag = false;

        InputStreamReader contentOfAsset = null;
        FileOutputStream outputStream = null;
        File destFile = new File(context.getExternalFilesDir(null),"tessdata/eng.traineddata");

        try {
            contentOfAsset = new InputStreamReader(context.getAssets().open("eng.traineddata"));
            if(destFile.exists()){
                //Le fichier existe déja
                Toast.makeText(context,"Le ficier existe déja",Toast.LENGTH_SHORT).show();
            }else{
                //Il n'existe pas encore donc on le crée
                boolean fileCreated = destFile.createNewFile();
                if(fileCreated){
                    outputStream = new FileOutputStream(destFile);
                    if(outputStream!=null && contentOfAsset!=null){
                        int n;
                        while ((n = contentOfAsset.read()) != -1){
                            outputStream.write(n);
                        }
                    }
                }
                Toast.makeText(context,"Le ficier a été crée",Toast.LENGTH_SHORT).show();
                //new MaterialAlertDialogBuilder(context).setMessage("Le fichier a été créé").show();
            }
            fileExistFlag = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            if (fileExistFlag) {
                try {
                    if (contentOfAsset != null) contentOfAsset.close();
                    String pathData = new File(context.getExternalFilesDir(null),"").getAbsolutePath();
                    boolean initialisationComplted = mTess.init(pathData,language);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if(contentOfAsset!=null){
                try {
                    contentOfAsset.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /*String dstPathDir = "/tesseract/tessdata/";

        String srcFile = "eng.traineddata";
        InputStream inFile = null;

        dstPathDir = context.getFilesDir() + dstPathDir;
        String dstInitPathDir = "/storage/emulated/0" + "/tesseract";
        String dstPathFile = dstPathDir + srcFile;
        FileOutputStream outFile = null;

        try {
            inFile = assetManager.open(srcFile);

            File f = new File(dstPathDir);

            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Toast.makeText(context, srcFile + " can't be created.", Toast.LENGTH_SHORT).show();
                }
                outFile = new FileOutputStream(new File(dstPathFile));
            } else {
                fileExistFlag = true;
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

        } finally {

            if (fileExistFlag) {
                try {
                    if (inFile != null) inFile.close();
                    mTess.init(dstInitPathDir, language);
                    return;

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }

            if (inFile != null && outFile != null) {
                try {
                    //copy file
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inFile.read(buf)) != -1) {
                        outFile.write(buf, 0, len);
                    }
                    inFile.close();
                    outFile.close();
                    mTess.init(dstInitPathDir, language);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            } else {
                Toast.makeText(context, srcFile + " can't be read.", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);

        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
