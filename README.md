# ElasticSearch Slack explorer

The simple app allow to make simple queries to an ElasticSearch via slack.
Also you can subscribe and perform queryes periodically.
 
## Run

You need Java 8 to run and scala/sbt to compile.

 * Compile `sbt one-jar`
 * run `java -jar target/scala-2.11/elaslack_2.11-1.0-one-jar.jar --slackToken="..." --slackChannel="#general" --elasticUrl="..." --elasticUser="..." --elasticPass="..."`
 
After start introduction will be sended to the slack channel.