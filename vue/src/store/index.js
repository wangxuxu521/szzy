import { createStore } from "vuex";
import user from "./modules/user";
import resource from "./modules/resource";

const store = createStore({
  modules: {
    user,
    resource,
  },
  strict: process.env.NODE_ENV !== "production",
});

export default store;
