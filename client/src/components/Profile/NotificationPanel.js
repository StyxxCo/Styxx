import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/NotificationPanel.css'
import Notification from './Notification'
import { getMessages } from '../../redux/actions'

class NotificationPanel extends Component {


  render() {
    const { notifications } = this.props
    return (
      <div className="NotificationPanel">
        <h4 className="notification-header"> Notifications </h4>
        <hr/>
        { notifications 
          && <div>
              {Object.keys(notifications).map((key, i) => {
                return <Notification
                  notification={notifications[key]}
                  i={i}
                  deleteNotification={this.props.deleteNotification}
                  acceptNotification={this.props.acceptNotification}
                  rejectNotification={this.props.rejectNotification}
                  markAsRead={this.props.markAsRead}
                  key={notifications[key].messageID}
                />
              })}
            </div>
        }
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    notifications: state.messages ? state.messages.data : null,
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getMessages: (url, header) => dispatch(getMessages(url, header)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(NotificationPanel)
