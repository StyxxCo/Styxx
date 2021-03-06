function user(state = {}, action) {
    switch(action.type) {
        case 'LOG_IN': 
            return Object.assign({}, state, {
                data: action.user,
            })
        case 'LOG_OUT':
            return Object.assign({}, state, {
                data: null,
            })
        case 'UPDATE_USER':
            return Object.assign({}, state, {
                data: action.data,
            })
        case 'ADD_SKILLS': 
            return Object.assign({}, state, {
                skills: state.skills ? [...state.skills].concat(action.skills) : action.skills
            })
        case 'ADD_NEW_SKILL':
            let temp
            if(state.newSkills) {
                temp = [...state.newSkills]
                temp.push(action.skill)
            }
            return Object.assign({}, state, {
                newSkills: state.newSkills ? temp : [action.skill]
            })
        case 'DELETE_NEW_SKILL':
            return Object.assign({}, state, {
                newSkills: state.newSkills ? [...state.newSkills].splice(state.newSkills.indexOf(action.skill), 1) : []
            })
        case 'CLEAR_NEW_SKILLS':
            return Object.assign({}, state, {
                newSkills: []
            })
        case 'SET_NEW_SKILLS': 
            return Object.assign({}, state, {
                newSkills: action.skills
            })
        case 'SET_CITY':
            return Object.assign({}, state, {
                city: action.city
            })
        case 'ADD_OBJECT_ID':
            return Object.assign({}, state, {
                accountID: action.id
            })
        case 'CLEAR_OBJECT_ID':
            return Object.assign({}, state, {
                accountID: null
            })
        case 'ADD_IS_MOD':
            return Object.assign({}, state, {
                isMod: action.isMod
            })      
        default:
            return state
    }
}

export default user