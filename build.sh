mvn clean
mvn compile
mvn package
cp bot-settings.json target/bot-settings.json
cd target
date >> cronlog.txt && java -jar final-alfredbot.jar >> cronlog.txt
