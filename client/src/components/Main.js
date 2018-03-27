import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router'
import NotificationSystem from 'react-notification-system'
import { Button, ButtonGroup, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
import SockJsClient from 'react-stomp'
import { connect } from 'react-redux'

import '../css/Main.css'
import Navbar from './Navbar'
import CreateGroup from './CreateGroup'
import Groups from './Groups/Groups'
import PublicGroups from './PublicGroups'
import Profile from './Profile/Profile'
import Settings from './Settings'
import SearchResults from './SearchResults'
import url from '../server'
import { loadNotifications, getGroups } from '../redux/actions'


class Main extends Component {

  constructor(props) {
    super(props)

    this.state = {
      modal: false,
      groupID: '',
    }
  }

  _notificationSystem = null

  componentDidMount =  () => {
    this._notificationSystem = this.refs.notificationSystem
  }

  handleNotification = (data) => {
    if(data && data[0] && !this.props.notifications) {
      this.props.loadNotifications(data)
    } else if(data) {
      switch (data.type) {
        case 0:
          // Group invite
          this._notificationSystem.addNotification({
            title: 'Group Invite',
            message: data.content,
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={this.joinGroup}>Join</Button>
                <Button color="danger" onClick={this.ignoreGroup}>Ignore</Button>
              </ButtonGroup>
            )
          })
          break
        case 1:
          // Friend invite
          this._notificationSystem.addNotification({
            title: 'Friend Invite',
            message: data.content,
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={this.acceptFriendRequest}>Accept</Button>
                <Button color="danger" onClick={this.rejectFriendRequest}>Reject</Button>
              </ButtonGroup>
            )
          })
          break
        case 2:
          // Mod Warning
          this._notificationSystem.addNotification({
            title: 'Warning',
            message: data.content,
            level: 'warning',
            action: {
              label: 'View Warning',
              callback: this.showWarning
            }
          })
          break
        case 3: 
          // Kick Notification
          this._notificationSystem.addNotification({
            title: 'Kick',
            message: data.content,
            level: 'error',
          })
          break
        case 4:
          // Join Request
          this._notificationSystem.addNotification({
            title: 'Join request',
            message: data.content,
            level: 'success',
            action: {
              label: 'Allow',
              callback: this.joinGroup
            }
          })
          break
        case 5:
          // Rate request
          this._notificationSystem.addNotification({
            title: 'Rate request',
            message: data.content,
            level: 'success',
            action: {
              label: 'Rate!',
              callback: () => this.rate,
            }
          })
          break
        default:
          // Basic Notification
          // Add basic messages here if necessary
      }
    }
  }

  rate = () => {

  }

  showWarning = () => {

  }

  acceptFriendRequest = () => {
  
  }

  rejectFriendRequest = () => {

  }

  joinGroup = () => {

  }

  ignoreGroup = () => {

  }

  inviteToGroup = (groupID, accountID) => {
    this.clientRef.sendMessage(`/app/inviteToGroup/${this.props.accountID}/${accountID}/${groupID}`)
  }

  sendFriendRequest = (friendID) => {
    this.clientRef.sendMessage(`/app/requestFriend/${this.props.accountID}/${friendID}`)
  }

  allowRating = (groupID, accountID) => {
    
  }

  onWebsocketConnect = () => {
    this.clientRef.sendMessage(`/app/${this.props.accountID}/allMessages`, "");
  }

  getWebsocket = () => {
    if(this.props.accountID) {
      return <SockJsClient url={`${url}/sockJS`} topics={[`/notification/${this.props.accountID}`]}
          onMessage={this.handleNotification}
          onConnect={this.onWebsocketConnect}
          ref={ (client) => { this.clientRef = client }} 
          subscribeHeaders={{ 'X-Authorization-Firebase': this.props.token }}
          headers={{ 'X-Authorization-Firebase': this.props.token }}
          debug
        />
    }
    return 
  }

  renderMemberList = () => {

  }

  render() {
    return (
      <div className="Main h-100">
        <Navbar {...this.props} />
        <Switch>
            <Route path="/create" render={(navProps) => <CreateGroup {...navProps} />}/>
            <Route path="/groups" render={(navProps) => <Groups {...navProps} {...this.props} allowRating={this.allowRating} />}/>
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} />}/>
            <Route path="/profile/:ownerID" render={(navProps) => <Profile {...navProps} sendFriendRequest={this.sendFriendRequest} inviteToGroup={this.inviteToGroup} />}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path="/search/:category/:query" render={(navProps) => <SearchResults {...navProps} sendFriendRequest={this.sendFriendRequest} goToProfile={this.props.goToProfile}/>}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>
        
        {this.getWebsocket()}
        <NotificationSystem ref="notificationSystem" allowHTML={this.props.allowHTML} />
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    token: state.auth.token,
    accountID: state.user.accountID,
    notifications: state.messages.data,
    groups: state.groups ? state.groups.getGroupsData : [],
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    loadNotifications: (notifications) => dispatch(loadNotifications(notifications)),
    getGroups: (url, headers) => dispatch(getGroups(url, headers)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Main)
