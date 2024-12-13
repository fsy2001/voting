let selectedNomineeId

async function clickApproveButton(button) {
    selectedNomineeId = button.getAttribute('value')
    await approveNominee()
}

function clickRejectButton(button) {
    selectedNomineeId = button.getAttribute('value')
    let dialog = document.getElementById('reject-dialog')
    dialog.show()
}

function clickChangeVoteButton(button) {
    selectedNomineeId = button.getAttribute('value')
    let dialog = document.getElementById('change-vote-dialog')
    dialog.show()
}

async function approveNominee() {
    let response = await fetch(`/api/admin/approve-song?approve=true&songId=${selectedNomineeId}`)
    let message = await response.text()
    let dialog = new Dialog()
    dialog.alert(message, {type: response.ok ? 'success' : 'danger'})
}

async function rejectNominee() {
    let form = document.getElementById('reject-dialog')
    form.close()

    let textField = document.getElementById('reject-reason')
    let reason = textField.value
    let response = await fetch(`/api/admin/approve-song?approve=false&songId=${selectedNomineeId}&reason=${reason}`)
    let message = await response.text()
    let dialog = new Dialog()
    dialog.alert(message, {type: response.ok ? 'success' : 'danger'})
}

async function changeVote() {
    let form = document.getElementById('change-vote-dialog')
    form.close()

    let textField = document.getElementById('change-vote')
    let newVote = textField.value
    let response = await fetch(`/api/admin/change-vote?songId=${selectedNomineeId}&newVote=${newVote}`)
    let message = await response.text()
    let dialog = new Dialog()
    dialog.alert(message, {type: response.ok ? 'success' : 'danger'})
}

async function createAdmin() {
    let textField = document.getElementById('create-admin-input')
    let userId = textField.value
    let response = await fetch(`/api/admin/create-admin?userId=${userId}`)
    let message = await response.text()
    let dialog = new Dialog()
    dialog.alert(message, {type: response.ok ? 'success' : 'danger'})
}