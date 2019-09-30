import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class RecordAppender {
	public static void main(final String[] args) throws IOException {
		List<String> rawData = Arrays.asList("jsmith:x:1001:1000:Joe Smith,Room 1007,(234)555-8917,(234)555-0077,[email protected]:/home/jsmith:/bin/bash",
                "jdoe:x:1002:1000:Jane Doe,Room 1004,(234)555-8914,(234)555-0044,[email protected]:/home/jdoe:/bin/bash",
                "xyz:x:1003:1000:X Yz,Room 1003,(234)555-8913,(234)555-0033,[email protected]:/home/xyz:/bin/bash");

		List<Record> records = rawData.stream().map(Record::parse).collect(Collectors.toList());
		Path tmp = Paths.get("_rosetta", ".passwd");
		Files.createDirectories(tmp.getParent());
		Files.write(tmp, (Iterable<String>) records.stream().limit(2).map(Record::toString)::iterator);

		Files.write(tmp, Collections.singletonList(records.get(2).toString()), StandardOpenOption.APPEND);

		try (Stream<String> lines = Files.lines(tmp)) {
			lines.map(Record::parse).forEach(System.out::println);
		}
	}
}

class Record {
	private final String account;
	private final String password;
	private final int uid;
	private final int gid;
	private final List<String> gecos;
	private final String directory;
	private final String shell;

	public Record(final String account, final String password, final int uid, final int gid,
			final List<String> gecos, final String directory, final String shell) {
		this.account = account;
		this.password = password;
		this.uid = uid;
		this.gid = gid;
		this.gecos = gecos;
		this.directory = directory;
		this.shell = shell;
	}

	@Override
	public String toString() {
		return account + ":" + password + ":" + uid + ":" + gid + ":" + 
			String.join(",", gecos) + ":" + directory + ":" + shell;
	}

	public static Record parse(String text) {
		String[] tokens = text.split(":");
		return new Record(
				tokens[0],
				tokens[1],
				Integer.parseInt(tokens[2]),
				Integer.parseInt(tokens[3]),
				Arrays.asList(tokens[4].split(",")),
				tokens[5],
				tokens[6]);
	}
}
