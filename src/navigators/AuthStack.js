import { createStackNavigator } from 'react-navigation';
import Login from '../screens/Login';

const AuthStack = createStackNavigator(
    {
        Login: {
            screen: Login
        }
    },
    {
        initialRouteName: 'Login',
    }
);

export default AuthStack;
