import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";

export const AuthApi = createApi (
    {
        reducerPath: "AuthApi",
        baseQuery: fetchBaseQuery({BaseUrl:"http://localhost:8080/api/items"}),
        endpoints:(builder) =>({
            addUser:builder.mutation({
                query:(userData) =>({
                    url: "/register",
                    method: "POST",
                    body:userData,
                }),
            }),
        }),
     });

export const {
    useAddUserMutation,

}  = AuthApi;


