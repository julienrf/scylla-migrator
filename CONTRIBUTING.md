# Contributing to scylla-migrator

## Testing procedure

Tests are implemented in the `tests` sbt submodule. They simulate the submission of a Spark job as described in the README. Therefore, before running the tests you need to build the migrator fat-jar and to set up a stack with a Spark cluster and databases to read data from and to write to.

1. Build the migrator fat-jar and its dependencies

   ~~~ sh
   ./build.sh
   ~~~
   
2. Set up the testing stack with Docker
   
   ~~~ sh
   docker compose -f docker-compose-tests.yml up
   ~~~

3. Run the tests

   ~~~ sh
   sbt test
   ~~~

   Or, to run a single test:

   ~~~ sh
   sbt testOnly com.scylladb.migrator.BasicMigrationTest
   ~~~

4. Ultimately, stop the Docker containers

   ~~~ sh
   docker compose -f docker-compose-tests.yml down
   ~~~

Make sure to re-build the fat-jar everytime you change something in the implementation:

~~~ sh
sbt migrator/assembly
~~~

And then re-run the tests.

## Debugging

The tests involve the execution of code on several locations:
- locally (ie, on the machine where you invoke `sbt test`): tests initialization and assertions
- on the Spark master node: the `Migrator` entry point
- on the Spark worker node: RDD operations

In all those cases, it is possible to debug them by using the Java Debug Wire Protocol.

### Local Debugging

Follow the procedure documented [here](https://stackoverflow.com/a/15505308/561721).

### Debugging on the Spark Master Node

1. In the file `MigratorSuite.scala`, uncomment the line that sets the
   `spark.driver.extraJavaOptions`.
2. Set up the remote debugger of your IDE to listen to the port 5005.
3. Run a test
4. When the test starts a Spark job, it waits for the remote debugger
5. Start the remote debugger from your IDE.
6. The test execution resumes, and you can interact with it from your debugger.

### Debugging on the Spark Worker Node

1. In the file `MigratorSuite.scala`, uncomment the line that sets the
   `spark.executor.extraJavaOptions`.
2. Set up the remote debugger of your IDE to listen to the port 5006.
3. Run a test
4. When the test starts an RDD operation, it waits for the remote debugger.
   Note that the Spark master node will not display the output of the worker node,
   but you can see it in the worker web UI: http://localhost:8081/.
5. Start the remote debugger from your IDE.
6. The test execution resumes, and you can interact with it from your debugger.

## Publishing

Create a new [GitHub release](https://github.com/scylladb/scylla-migrator/releases), give it a tag name, a title, and a description, and then click Publish. A workflow will be triggered and will build the application fat-jar and upload it as a release asset.
