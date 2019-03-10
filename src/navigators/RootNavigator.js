import { createSwitchNavigator, createAppContainer } from 'react-navigation';
import AppStack from './AppStack';
import AuthStack from './AuthStack';

const SwitchNavigator = createSwitchNavigator(
    {
        App: AppStack,
        Auth: AuthStack,
    },
    {
        initialRouteName: 'Auth',
    }
);

const RootNavigator = createAppContainer(SwitchNavigator);

export default RootNavigator;
