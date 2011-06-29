package org.nodex.core.parsetools;

import org.nodex.core.Callback;
import org.nodex.core.buffer.Buffer;

import java.io.UnsupportedEncodingException;

/**
 * User: tim
 * Date: 27/06/11
 * Time: 17:49
 */
public class RecordParser extends Callback<Buffer> {

  private Buffer buff;
  private int pos;            // Current position in buffer
  private int start;          // Position of beginning of current record
  private int delimPos;       // Position of current match in delimeter array
  private boolean reset;      // Allows user to toggle mode / change delim when records are emitted

  private boolean delimited;
  private byte[] delim;
  private int recordSize;
  private final Callback<Buffer> output;

  private RecordParser(Callback<Buffer> output) {
    this.output = output;
  }

  public static RecordParser newDelimited(final String delim, final String enc, final Callback<Buffer> output) {
    return newDelimited(delimToByteArray(delim, enc), output);
  }

  public static RecordParser newDelimited(byte[] delim, Callback<Buffer> output) {
    RecordParser ls = new RecordParser(output);
    ls.delimitedMode(delim);
    return ls;
  }

  public static RecordParser newFixed(int size, Callback<Buffer> output) {
    RecordParser ls = new RecordParser(output);
    ls.fixedSizeMode(size);
    return ls;
  }

  private static byte[] delimToByteArray(String delim, String enc) {
   try {
      return delim.getBytes(enc);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unsupported encoding " + enc);
    }
  }

  public void delimitedMode(final String delim, final String enc) {
    delimitedMode(delimToByteArray(delim, enc));
  }

  public void delimitedMode(byte[] delim) {
   delimited = true;
   this.delim = delim;
   delimPos = 0;
   reset = true;
  }

  public void fixedSizeMode(int size) {
    delimited = false;
    recordSize = size;
    reset = true;
  }

  private void handleParsing() {
    int len = buff.length();
    do {
      reset = false;
      if (delimited) {
        parseDelimited();
      } else {
        parseFixed();
      }
    } while (reset);

    if (start == len) {
      //Nothing left
      buff = null;
      pos = 0;
    } else {
      buff = buff.copy(start, len);
      pos = buff.length();
    }
    start = 0;
  }

  private void parseDelimited() {
    int len = buff.length();
    for (; pos < len && !reset; pos++) {
      if (buff.byteAt(pos) == delim[delimPos]) {
        delimPos++;
        if (delimPos == delim.length) {
          Buffer ret = buff.slice(start, pos + 1);
          start = pos + 1;
          delimPos = 0;
          output.onEvent(ret);
        }
      }
    }
  }

  private void parseFixed() {
    int len = buff.length();
    while (len - start >= recordSize && !reset) {
      int end = start + recordSize;
      Buffer ret = buff.slice(start, end);
      start = end;
      output.onEvent(ret);
    }
  }

  public void onEvent(Buffer buffer) {
    if (buff == null) {
      buff = Buffer.newDynamic(buffer.length());
    }
    buff.append(buffer);
    handleParsing();
  }
}