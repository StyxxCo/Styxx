import React, { Component } from 'react'
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';
import { connect } from 'react-redux'

import '../css/CreateGroup.css'
import SkillsForm from './Profile/SkillsForm'
import { addSkills, clearNewSkills } from '../redux/actions'

class CreateGroup extends Component {

  handleSubmit = (ev) => {
    ev.preventDefault()
    const preferredSkills = this.props.newSkills
    const groupName = ev.target.name.value
    const purpose = ev.target.purpose.value
    const reputation = ev.target.reputation.value

    this.props.clearSkills()
    ev.target.reset()

    //create group API call here, redux
    //Get group ID and redirect to group page
  }
  
  render() {
    return (
      <div className="CreateGroup">
        <h3>Create A Group</h3>
        <Form className="create-group-form" id="form" onSubmit={this.handleSubmit}>
          <FormGroup className="required">
            <Label for="name">Group Name</Label>
            <Input autoFocus required type="text" name="name" id="name" />
          </FormGroup>
          <FormGroup>
          <Label for="purpose">Purpose</Label>
          <Input type="textarea" name="purpose" id="purpose" />
        </FormGroup>
          <FormGroup className="required">
            <Label for="reputation">Minimum Reputation</Label>
            <Input required type="number" name="reputation" id="repuation" min={0} defaultValue={0} />
          </FormGroup>
        </Form>
        <div className="skills-form">
          <Label for="skills">Preferred Skills</Label>
          <SkillsForm id="skills" autoFocus={false}/>
        </div>
        <Button type="submit" onSubmit={this.handleSubmit} form="form">Submit</Button>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    newSkills: state.user.newSkills,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    clearSkills: () => dispatch(clearNewSkills()),
    addSkills: (skills) => dispatch(addSkills(skills))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateGroup)

