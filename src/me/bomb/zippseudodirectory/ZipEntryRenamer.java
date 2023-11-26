package me.bomb.zippseudodirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipEntryRenamer extends Thread {
	
	private final File sourcezip, destinationzip;
	private final HashMap<String, String> rename;
	private final AtomicBoolean ok = new AtomicBoolean();
	
	public ZipEntryRenamer(File sourcezip, File destinationzip, HashMap<String, String> rename) {
		this.sourcezip = sourcezip;
		this.destinationzip = destinationzip;
		this.rename = new HashMap<String, String>(rename);
		start();
	}
	
	public void run() {
		if(sourcezip == null || destinationzip == null || !sourcezip.isFile()) {
			return;
		}
		ZipInputStream zfis = null;
		ZipOutputStream zfos = null;
		
		try {
			zfis = new ZipInputStream(new FileInputStream(sourcezip));
		} catch (IOException e) {
			return;
		}
		try {
			zfos = new ZipOutputStream(new FileOutputStream(destinationzip, false));
		} catch (IOException e) {
			try {
				zfis.close();
			} catch (IOException e1) {
			}
			return;
		}
		
		ZipEntry zipEntry = null;
        try {
        	byte[] buffer = new byte[1024];
			while ((zipEntry = zfis.getNextEntry()) != null) {
				zipEntry = transformEntry(zipEntry);
				zfos.putNextEntry(zipEntry);
				int len;
                while ((len = zfis.read(buffer)) > 0) {
                	zfos.write(buffer, 0, len);
                }
			}
		} catch (IOException e1) {
		}
		
		try {
			zfis.close();
		} catch (IOException e) {
		}
		try {
			zfos.close();
		} catch (IOException e) {
		}
		ok.set(true);
	}
	
	public boolean isOk() {
		return ok.get();
	}
	
	private ZipEntry transformEntry(ZipEntry entry) {
		String entryname = entry.getName(), newentryname = rename.remove(entryname);
		ZipEntry newzipentry = new ZipEntry(newentryname==null ? entryname:newentryname);
		newzipentry.setComment(entry.getComment());
		FileTime creationtime = newzipentry.getCreationTime(), lastaccesstime = newzipentry.getLastAccessTime(), lastmodifiedtime = newzipentry.getLastModifiedTime();
		if(creationtime!=null) newzipentry.setCreationTime(creationtime);
		if(lastaccesstime!=null)newzipentry.setLastAccessTime(lastaccesstime);
		if(lastmodifiedtime!=null)newzipentry.setLastModifiedTime(lastmodifiedtime);
		try {
			newzipentry.setMethod(newzipentry.getMethod());
		} catch (IllegalArgumentException e) {
		}
		return newzipentry;
	}
}
