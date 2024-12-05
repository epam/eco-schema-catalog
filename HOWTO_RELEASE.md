[Official guide from Sonatype](https://central.sonatype.org/pages/producers.html)

### How to upload artifact to Sonatype staging repository for Maven Central promoting

  1. [Configure PGP](https://central.sonatype.org/pages/working-with-pgp-signatures.html)
  2. Update your maven settings.xml with valid values (See settings.xml for the quick help).
  3. `mvn release:clean release:prepare`.
  4. [How to generate access token to use it in settings.xml](https://central.sonatype.org/publish/generate-token/)
     Access via user + password now is disabled.
  5. After successful run you will be able to find tag in the vcs for new version of your artifact.
  6. `mvn release:perform -Darguments="-DskipTests"`.
  7. After successful run you will be able to find your artifact in the [Sonatype staging repository](https://oss.sonatype.org/#stagingRepositories).
  8. Because of `<autoReleaseAfterClose>false</autoReleaseAfterClose>` for `nexus-staging-maven-plugin` 
  you will see 2 buttons `Release` and `Drop`, pay attention that Maven Central Release can't be undone, that is why
  double check all that you push to the [Sonatype staging repository](https://oss.sonatype.org/#stagingRepositories)
  and only then press the `Release`. `Drop` button simply delete your artifact from 
  [Sonatype staging repository](https://oss.sonatype.org/#stagingRepositories). If you feel confidence you can just set
  `<autoReleaseAfterClose>true</autoReleaseAfterClose>` for `nexus-staging-maven-plugin`, in this situation 
   release to the Maven Central will be performed automatically without extra steps with buttons (`Release`, `Drop`).
   As an alternative way of performing Maven Central Release you can use `nexus-staging-maven-plugin` goals:
   `nexus-staging:release` as equivalent for `Release` button on the UI of the
   [Sonatype staging repository](https://oss.sonatype.org/#stagingRepositories) and `nexus-staging:drop`
   as equivalent for `Drop` button.
  
### Notes

['-SNAPSHOT' deployments repository](https://oss.sonatype.org/content/repositories/snapshots/)
