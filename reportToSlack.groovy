import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLConnection

def environment = System.getenv()
def SLACK_URL_HOOK = environment['Slack_URL']
def CHANNEL = environment['Slack_Channel']
def USERNAME = "GoCD"
def EMOJI = "slack"
def TITLE_1 = "New Build - Automated Notification"
def COLOR = "good"
def revision = environment['GO_REVISION']
def mentions = environment['Slack_Mentions']

def proc = "svn log --verbose --revision ${revision}".execute()
def b = new StringBuffer()
proc.consumeProcessErrorStream(b)

def MSG_1 = proc.text
def artifactsLink = environment['GO_SERVER_URL'] + "/tab/build/detail/" + environment['GO_PIPELINE_NAME'] + "/" + environment['GO_PIPELINE_COUNTER'] + "/" + environment['GO_STAGE_NAME'] + "/" + environment['GO_STAGE_COUNTER'] + "/" + environment['GO_JOB_NAME']

def body = """ 
{
"channel" : "#${CHANNEL}",
"username" : "${USERNAME}",
"icon_emoji" : ":${EMOJI}:",
"attachments" : [
  {
    "fallback" : "${TITLE_1}",
    "color" : "${COLOR}",
    "fields" : [
      {
        "title" : "${TITLE_1}",
        "value" : "Commit Log: \n${MSG_1}\nArtifacts are under: ${artifactsLink}\n${mentions}"
       }
     ]
   }
 ]
}
"""

def sendPostRequest(urlString, paramString) {
    def url = new URL(urlString)
    def conn = url.openConnection()
    conn.setDoOutput(true)
    def writer = new OutputStreamWriter(conn.getOutputStream())

    writer.write(paramString)
    writer.flush()
    String line
    def reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))
    while ((line = reader.readLine()) != null) {
      println line
    }
    writer.close()
    reader.close()
}

sendPostRequest(SLACK_URL_HOOK,body)