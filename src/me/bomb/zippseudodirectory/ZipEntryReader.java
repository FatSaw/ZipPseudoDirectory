package me.bomb.zippseudodirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipEntryReader extends Thread {
	private final File sourcezip;
	private final Set<String> entrys = new HashSet<String>();
	public ZipEntryReader(File sourcezip) {
		this.sourcezip = sourcezip;
		start();
	}
	
	public void run() {
		if(sourcezip == null || !sourcezip.isFile()) {
			return;
		}
		ZipInputStream zfis = null;
		try {
			zfis = new ZipInputStream(new FileInputStream(sourcezip));
		} catch (IOException e) {
			return;
		}
		
		ZipEntry zipEntry = null;
        try {
			while ((zipEntry = zfis.getNextEntry()) != null) {
				entrys.add(zipEntry.getName());
			}
		} catch (IOException e1) {
		}
        
		try {
			zfis.close();
		} catch (IOException e) {
		}
	}
	
	public Set<String> getEntrys() {
		if(isAlive()) return null;
		return entrys;
	}
}
