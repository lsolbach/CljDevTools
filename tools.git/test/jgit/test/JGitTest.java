package jgit.test;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;



public class JGitTest {

	public static void main(String[] args) {
		File gitDir = new File("/home/soulman/devel/tmp/jgittest/.git");
		//File gitDir = new File("/home/soulman/devel/workspaces/gitWS/CljLibrary/.git");
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repo = builder.setGitDir(gitDir).readEnvironment().findGitDir().build();
			Git git = new Git(repo);
			Status status = git.status().call();
			System.out.println("Status: ");
			System.out.println("  added                 " + status.getAdded().size());
			System.out.println("  changed               " + status.getChanged().size());
			System.out.println("  conflicting           " + status.getConflicting().size());
			System.out.println("  conflictingStageState " + status.getConflicting().size());
			System.out.println("  ignoredNotInIndex     " + status.getIgnoredNotInIndex().size());
			System.out.println("  missing               " + status.getMissing().size());
			System.out.println("  modified              " + status.getModified().size());
			System.out.println("  removed               " + status.getRemoved().size());
			System.out.println("  untracked             " + status.getUntracked().size());
			System.out.println("  untrackedFolders      " + status.getUntrackedFolders().size());
			git.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
