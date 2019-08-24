import React, {PropTypes} from 'react';

class ProfilePage extends React.Component {
  render() {
    return (
        <div>
        <ProfileArea
          username="test"
          emailAddress="test@user.com"
        />
      </div>
    );
  }

}

ProfilePage.propTypes = {
};

export default ProfilePage;