package decode;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public final class BitInputStream implements AutoCloseable {

	private final InputStream input;
	private int currentByte;
	private int numBitsRemaining;

	public BitInputStream(InputStream in) {
		input = Objects.requireNonNull(in);
		currentByte = 0;
		numBitsRemaining = 0;
	}

	public int read() throws IOException {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			currentByte = input.read();
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		if (numBitsRemaining <= 0)
			throw new AssertionError();
		numBitsRemaining--;
		return (currentByte >>> numBitsRemaining) & 1;
	}
	

	public int readNoEof() throws IOException {
		int result = read();
		if (result != -1)
			return result;
		else
			throw new EOFException();
	}

	public void close() throws IOException {
		input.close();
		currentByte = -1;
		numBitsRemaining = 0;
	}
	
}
