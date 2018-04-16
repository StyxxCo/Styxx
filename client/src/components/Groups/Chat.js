import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert, Badge, Button, InputGroupAddon, Input, InputGroup} from 'reactstrap'
import SockJsClient from 'react-stomp'

import '../../css/Chat.css'
import { getChatLog, postChatMessage, updateChatLog } from '../../redux/actions'
import url from '../../server'

const Message = ({message, sender, align, time}) => {
  if (!message) return <div></div>;
  var time1 = ""
  var time2 = ""
  if (align == "sender-message-right"){
    time1 = time
  }else{
    time2 = time
  }
  return (
    <div className={align}>
      <Badge className={align.concat("-badge")}>{sender}</Badge>
      <br/>
      <span className="time-stamp-right">{time1}</span>
      <Alert className="single-message" className={align.concat("-alert")}> {message} </Alert>
      <span className="time-stamp-left">{time2}</span>
    </div>);
};

class Chat extends Component {

  constructor(props) {
    super(props);

    this.state = {
      messagesEnd: "",
      messages: [],
      msgInput: ""
    }
    this.handleChange = this.handleChange.bind(this);
    this.sendMessage = this.sendMessage.bind(this);
  }

  handleChange(event) {
    this.setState({msgInput: event.target.value});
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.group != this.props.group){
      this.state.messages = [];
      this.setState(this.state);
      setTimeout(() => this.onWebsocketConnect(), 1)
    }
  }

  sendMessage = (event) => {
    event.preventDefault();
    if (this.state.msgInput == "") {
      return;
    }
    const msg = {
      senderId: this.props.user.uid,
      content: this.state.msgInput,
    }
    this.clientRef.sendMessage('/app/' + this.props.user.uid + '/' 
                               + this.props.group.groupID + '/' + this.state.msgInput +'/sendMessage');
    this.state.msgInput = "";
    event.target.reset();
  }

  handleMessage = (data) => {
    if (data[0]){
      this.state.messages = data.map( (m) => {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(m.creationTime["hour"] > 12){
          hour = (m.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          hour = m.creationTime["hour"] + ":"
          ampm = " AM"
        }
        if(Math.floor(m.creationTime["minute"] / 10) == 0){
          minute = "0" + m.creationTime["minute"]
        }else{
          minute = m.creationTime["minute"]
        }
        time = hour + minute + ampm
        return {sender: m.senderName, message: m.content, id: m.senderID, 
          date: m.creationDate["dayOfWeek"] + " " + m.creationDate["month"] + 
          " " + m.creationDate["dayOfMonth"], time: time}
      })
      this.setState(this.state)
    } else {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(data.creationTime["hour"] > 12){
          hour = (data.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          hour = data.creationTime["hour"] + ":"
          ampm = " AM"
        }
        if(Math.floor(data.creationTime["minute"] / 10) == 0){
          minute = "0" + data.creationTime["minute"]
        }else{
          minute = data.creationTime["minute"]
        }
        time = hour + minute + ampm
      this.state.messages.push({
        sender: data.senderName, message: data.content, id: data.senderID, 
        date: data.creationDate["dayOfWeek"] + " " + data.creationDate["month"] + 
        " " + data.creationDate["dayOfMonth"], time: time
      })
      this.setState(this.state)
    }
  }

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth" });
  }
  
  componentDidMount() {
    this.scrollToBottom();
    
  }
  
  componentDidUpdate() {
    this.scrollToBottom();
  }

  onWebsocketConnect() {
    if (this.props.group && this.clientRef.state.connected) {
      //YYYY-MM-DD
      this.clientRef.sendMessage('/app/'+ this.props.user.uid + '/' + this.props.group.groupID + '/messageHistory', "");
    }
  }

  getWebsocket() {
    if (this.props.group) {
      return <SockJsClient url={`${url}/sockJS`} topics={['/group/'+ this.props.user.uid + '/' + this.props.group.groupID, 
    '/group/' + this.props.group.groupID] }
          onMessage={this.handleMessage.bind(this)}
          onConnect={this.onWebsocketConnect.bind(this)}
          ref={ (client) => { this.clientRef = client }} 
          subscribeHeaders={{ 'X-Authorization-Firebase': this.props.token }}
          headers={{ 'X-Authorization-Firebase': this.props.token }}
          debug
        />
    } else {
      return;
    }
  }

  render() {
    const messages = this.state.messages;
    var passingDate = "";

    return (
      <div className="Chat">
        <div className="message-container">
          {
            messages.map((c, index) => {
              var dateDiv = <div></div>
              if( c.date != passingDate){
                var dateDiv = <div className="date-div-center"><Badge >{c.date}</Badge></div>
                passingDate = c.date
              } 
              if( c.id === this.props.user.uid ){
                return <div key={index}>{dateDiv}<Message align="sender-message-right" key={index} sender={c.sender} message={c.message} time={c.time}></Message></div>
              } else {
                return <div key={index}>{dateDiv}<Message align="sender-message-left" key={index} sender={c.sender} message={c.message} time={c.time}></Message></div>
              }
            })
          }
          <div ref={(el) => { this.messagesEnd = el; }}></div>
        </div>
        <div className="send-message-container">
        <form onSubmit={this.sendMessage}>
          <InputGroup>
            <Input value={this.state.value} onChange={this.handleChange}/>
            <InputGroupAddon addonType="append">
              <Button color="success">Send Message</Button>
            </InputGroupAddon>
          </InputGroup>
        </form>
        </div>
        {this.getWebsocket()}
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    token: state.auth.token,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getLog: (url, header) => dispatch(getChatLog(url, header)),
    postMessage: (url, header) => dispatch(postChatMessage(url, header)),
    updateLog: (message) => dispatch(updateChatLog(message)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Chat)
