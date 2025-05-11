import { configureStore} from "@reduxjs/toolkit";
import { AuthApi } from "../service/AuthApi";
import { setupListeners } from "@reduxjs/toolkit/query";


export const store = configureStore ({
    reducer : {
        [AuthApi.reducerPath]:AuthApi.reducer,
    },
 middleWare: (getDefaultMiddleWare) =>{
        return getDefaultMiddleWare().concat(AuthApi.middleware);
    },
});

setupListeners(store.dispatch);