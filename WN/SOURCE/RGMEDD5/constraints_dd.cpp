#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <climits>
// #include <math.h>
#include <algorithm>
// #include <numeric>
#include <memory>
#include <map>
#include <unordered_set>
// #include <set>
// #include <stack>
// #include <cstdint>

#include "rgmedd5.h"
#include "varorders.h"
// #include "bufferless_sort.h"
// #include "optimization_finder.h"
// #include "utils/thread_pool.h"
// #include "utils/mt64.h"
// #include "dlcrs_matrix.h"

#ifdef HAS_LP_SOLVE_LIB
#  undef NORMAL
#  include <lp_lib.h>
#else
#  warning "Missing lp_solve library. RGMEDD5 modules that use LP will not be compiled."
#endif // HAS_LP_SOLVE_LIB

#include "irank.h"


//---------------------------------------------------------------------------------------

struct edge_t {
    int value;
    size_t node_ref;
};

//---------------------------------------------------------------------------------------

class node_t {
    int lvl; // can be negative (terminal levels)
public:
    inline node_t(int _lvl=int(-100), size_t _num_constr=0) 
    : lvl(_lvl), psums(_num_constr, -100) { }

    node_t(const node_t& ps) = default;
    node_t(node_t&&) = default;
    node_t& operator=(const node_t&) = default;
    node_t& operator=(node_t&&) = default;

    std::vector<int> psums; // partial sums of the constraints
    std::vector<edge_t> ee; // value/node-ref pairs (edges)
    size_t precompted_hash;

    // join two partial sums into a single empty node
    inline node_t(const node_t& ps1, const node_t& ps2) 
    : lvl(max(ps1.lvl, ps2.lvl)), psums(ps1.num_psums() + ps2.num_psums())
    {
        size_t j=0;
        for (size_t i=0; i<ps1.num_psums(); i++)
            psums[j++] = ps1.psums[i];
        for (size_t i=0; i<ps2.num_psums(); i++)
            psums[j++] = ps2.psums[i];
    }

    inline ~node_t() { }

    void swap(node_t& ps);

    inline size_t num_psums() const { return psums.size(); }
    inline int level() const { return lvl; }
    inline bool is_terminal() const { return lvl<0; }
    inline bool is_true() const { return lvl == -1; }
    inline bool is_false() const { return lvl == -2; }

    void precompte_hash();

    // inline bool operator<(const node_t& rhs) const {
    //     assert(num_psums() == rhs.num_psums());
    //     if (lvl < rhs.lvl)
    //         return true;
    //     else if (lvl > rhs.lvl)
    //         return false;
    //     for (size_t cc=0; cc<num_psums(); cc++) {
    //         if (at(cc) < rhs.at(cc))
    //             return true; // *this < rhs
    //         else if (at(cc) > rhs.at(cc))
    //             return false; // *this > rhs
    //     }
    //     return false; // *this == rhs
    // }

    bool psums_equal(const node_t& rhs) const;
};
ostream& operator<<(ostream& os, const node_t& psum);

//---------------------------------------------------------------------------------------

void node_t::swap(node_t& ps) {
    std::swap(lvl, ps.lvl);
    std::swap(psums, ps.psums);
    std::swap(ee, ps.ee);
}

//---------------------------------------------------------------------------------------

ostream& operator<<(ostream& os, const node_t& node) {
    cout <<"lvl=";
    if (node.is_terminal())
        cout << (node.is_true() ? "T:" : "F:");
    else
        cout << node.level() << ":";
    for (size_t i=0; i<node.num_psums(); i++)
        os << (i==0?"":",") << node.psums[i];
    os << "[";
    for (size_t i=0; i<node.ee.size(); i++)
        os << (i==0?"":",") << node.ee[i].value;
    os << "]";

    return os;
}

//---------------------------------------------------------------------------------------

bool node_t::psums_equal(const node_t& rhs) const {
    // cout << "psums_equal " << (*this) << "   " << rhs << endl;
    assert(num_psums() == rhs.num_psums());
    if (lvl != rhs.lvl)
        return false;
    for (size_t cc=0; cc<num_psums(); cc++)
        if (psums[cc] != rhs.psums[cc])
            return false;
    return true;
}

//---------------------------------------------------------------------------------------

template<typename T>
inline T bit_rotl(const T value, int shift) {
    return (value << shift) | (value >> (sizeof(value)*CHAR_BIT - shift));
}

inline size_t hash_combine(size_t seed) { return seed; }

template <typename T, typename... Rest>
inline size_t hash_combine(size_t seed, T v, Rest&&... rest) {
    seed = std::hash<T>{}(v) + 0xd2911b3ffc2dd383 + bit_rotl(seed, 6);
    return hash_combine(seed, std::forward<Rest>(rest)...);
}

//---------------------------------------------------------------------------------------

void node_t::precompte_hash() {
    precompted_hash = 0x3ea4a8cdab71bde9;
    for (size_t cc=0; cc<num_psums(); cc++)
        precompted_hash = hash_combine(precompted_hash, size_t(psums[cc]));
}

//---------------------------------------------------------------------------------------




//---------------------------------------------------------------------------------------

class CDD_t;
struct hashed_node_t {
    size_t node_id;
    CDD_t* p_dd;

    bool operator==(const hashed_node_t& rhs) const;
};

namespace std {
    template<> struct hash<hashed_node_t> {
        std::size_t operator()(const hashed_node_t& k) const;
    };
}

//---------------------------------------------------------------------------------------







//---------------------------------------------------------------------------------------
// Constraint Decision Diagram
//---------------------------------------------------------------------------------------
class CDD_t {
    // Constraints table
    const flow_basis_metric_t& fbm;
    // Constraint indices in the footprint matrix fbm.B
    std::vector<size_t> constrs; 

    // Node table
    std::vector<node_t> forest;
    // Cache
    std::unordered_set<hashed_node_t> cache;

    static const size_t T;
    static const size_t F;

    // Root node
    size_t root_node_id;

    // Nodes by levels
public:
    CDD_t(const flow_basis_metric_t& _fbm, size_t cc) : fbm(_fbm), constrs{cc} { }

    inline size_t num_nodes() const { return forest.size(); }
    size_t num_edges() const;

    void initialize();
    bool collect_unused_nodes();
    void show(ostream& os) const;

    node_t next(const node_t& node, int value) const;

    void intersection(const CDD_t& c1, const CDD_t& c2);

    std::map<size_t, std::vector<bool>> mark_var_domains() const;

    // remove variable values from all nodes
    void intersect_var_domains(const std::vector<std::vector<bool>>& var_doms);

    // void swap(CDD_t&);

private:
    typedef std::map<std::pair<size_t, size_t>, size_t> intersection_op_cache_t;
    size_t intersect_recursive(const CDD_t& c1, const CDD_t& c2,
                               const size_t node_id1, const size_t node_id2,
                               intersection_op_cache_t& op_cache);

    size_t initialize_recursive(node_t&& node);

    typedef std::map<size_t, size_t> intersect_var_domains_cache_t;
    size_t intersect_var_domains_recursive(const std::vector<std::vector<bool>>& var_doms,
                                           const size_t node_id,
                                           intersect_var_domains_cache_t& op_cache);

    size_t add_node(node_t&& node);

    friend struct std::hash<hashed_node_t>;
    friend struct hashed_node_t;
};

//---------------------------------------------------------------------------------------

const size_t CDD_t::T = 1;
const size_t CDD_t::F = 0;

//---------------------------------------------------------------------------------------

namespace std {
    std::size_t hash<hashed_node_t>::operator()(const hashed_node_t& k) const {
        const node_t& node = k.p_dd->forest[k.node_id];
        return node.precompted_hash;
        return 0;
    }
};

bool hashed_node_t::operator==(const hashed_node_t& rhs) const {
    const node_t& n1 = p_dd->forest[node_id];
    const node_t& n2 = p_dd->forest[rhs.node_id];
    return n1.psums_equal(n2);
}

//---------------------------------------------------------------------------------------

void CDD_t::show(ostream& os) const {
    os << "CONSTRAINTS";
    for (size_t cc : constrs) 
        os << " " << cc;
    os << endl;

    for (int lvl=npl-1; lvl>=-1; lvl--) {
        int count = 0;

        for (const node_t& node : forest) {
            if (node.level() != lvl)
                continue;

            if (count==0) {
                const char* lvl_name;
                if (lvl >= 0)
                    lvl_name = tabp[ fbm.level_to_net[node.level()] ].place_name;
                else
                    lvl_name = "TERM";
                os << " @" << left << setw(5) << lvl_name;
            }
            os << " ";
            // print the psum
            for (size_t i=0; i<node.num_psums(); i++)
                os << (i==0?"":",") << node.psums[i];
            os << "[";
            // print the allowed assignments
            for (size_t i=0; i<node.ee.size(); i++)
                os << (i==0?"":",") << node.ee[i].value;
            os << "]";  
            count++;
        }
        if (count)
            os << endl;
    }
}

//---------------------------------------------------------------------------------------

size_t CDD_t::num_edges() const {
    size_t cnt = 0;
    for (const node_t& node : forest)
        cnt += node.ee.size();
    return cnt;
}

//---------------------------------------------------------------------------------------

void CDD_t::initialize() {
    assert(constrs.size() == 1);
    size_t icc = constrs[0];
    const int_lin_constr_t& constr = fbm.B[icc];
    forest.clear();

    // initialize the terminal elements
    forest.push_back(node_t(-2, 1)); // node 0
    forest[F].psums[0] = -1000;
    forest.push_back(node_t(-1, 1)); // node 1
    forest[T].psums[0] = constr.const_term;

    // initialize the root element
    node_t root_node(constr.coeffs.trailing(), 1);
    root_node.psums[0] = 0;

    // Fill the DD
    root_node_id = initialize_recursive(std::move(root_node));
}

//---------------------------------------------------------------------------------------

size_t CDD_t::initialize_recursive(node_t&& node) {
    // cout << "initialize_recursive: " << node << endl;
    assert(constrs.size() == 1);
    if (node.is_true())
        return node.psums_equal(forest[T]) ? T : F;
    if (node.is_false())
        return F;

    size_t icc = constrs[0];
    const int_lin_constr_t& constr = fbm.B[icc];

    const int plc = fbm.level_to_net[node.level()];
    const int bound = fbm.get_bound(plc);
    const int coeff = constr.coeffs[node.level()];

    // Add the links to downward nodes
    for (int v=0; v<=bound; v++) {
        node_t next_node = next(node, v);

        size_t new_node_id = initialize_recursive(std::move(next_node));
        if (new_node_id != F)
            node.ee.push_back(edge_t{.value=v, .node_ref=new_node_id});
    }
    // Add the node to the forest and to the cache
    size_t node_id = add_node(std::move(node));

    return node_id;
}

//---------------------------------------------------------------------------------------

size_t CDD_t::add_node(node_t&& node) {
    // cout << "add_node " << node << endl;
    if (node.ee.empty())
        return F;
    assert(!node.is_terminal());
    // if (node.is_true())
    //     return node.psums_equal(forest[T]) ? T : F;
    // if (node.is_false())
    //     return F;

    // Start by appending at the end of the forest
    size_t node_id = forest.size();
    node.precompte_hash();
    forest.emplace_back(node);
    // Search the node in the cache
    hashed_node_t hn {.node_id=node_id, .p_dd=this};
    auto it = cache.find(hn);
    if (it != cache.end()) { // node exists
        forest.pop_back();
        node_id = it->node_id;
        // cout << "USING " << node_id << " as " << forest[node_id] << endl;
    }
    else { // New node
        cache.emplace(std::move(hn));
        // cout << "NEW " << node_id << " as " << forest[node_id] << endl;
    }

    return node_id;
}

//---------------------------------------------------------------------------------------

node_t CDD_t::next(const node_t& node, int value) const {
    // determine the next level
    int next_lvl = -1;
    for (size_t cc=0; cc<constrs.size(); cc++) {
        const int_lin_constr_t& constr = fbm.B[constrs[cc]];
        if (node.level() <= constr.coeffs.leading()) { // leading term
            next_lvl = max(next_lvl, -1);
        }
        else if (node.level() > constr.coeffs.trailing()) { // trailing term
            next_lvl = max(next_lvl, int(constr.coeffs.trailing()));
        }
        else {
            const int ii = constr.coeffs.lower_bound_nnz(node.level());
            const int lvl_below = constr.coeffs.ith_nonzero(ii - 1).index;
            next_lvl = max(next_lvl, lvl_below);
        }
    }

    node_t next_node(next_lvl, node.num_psums());
    for (size_t cc=0; cc<constrs.size(); cc++) {
        const int_lin_constr_t& constr = fbm.B[constrs[cc]];
        next_node.psums[cc] = node.psums[cc] + constr.coeffs[node.level()] * value;
    }
    // cout << node <<" next value="<<value<<" is "<< next_node<<endl;
    return next_node;
}

// //---------------------------------------------------------------------------------------

bool CDD_t::collect_unused_nodes() {
    std::vector<bool> in_use(forest.size(), false);
    in_use[T] = in_use[F] = in_use[root_node_id] = true;

    // Mark nodes in use
    for (const node_t& node : forest) {
        if (node.is_terminal())
            continue;
        for (const edge_t& e : node.ee)
            in_use.at(e.node_ref) = true;
    }

    if (std::find(in_use.begin(), in_use.end(), false) == in_use.end())
        return false; // nothing to be removed

    std::vector<size_t> remap(forest.size());
    size_t count = 0;
    for (size_t i=0; i<forest.size(); i++) {
        if (in_use[i]) {
            remap[i] = count;
            std::swap(forest[count], forest[i]);
            count++;
        }
    }
    forest.resize(count);

    // Remap edges
    for (node_t& node : forest) {
        for (edge_t& edge : node.ee)
            edge.node_ref = remap[edge.node_ref];
    }

    // Rebuild the cache
    cache.clear();
    for (size_t i=0; i<forest.size(); i++) {
        cache.insert({.node_id=i, .p_dd=this});
    }

    // Remap the root node
    // cout << "root_node_id = " << root_node_id << " -> " << remap[root_node_id] << endl;
    root_node_id = remap[root_node_id];

    return true;
}

//---------------------------------------------------------------------------------------

void CDD_t::intersection(const CDD_t& c1, const CDD_t& c2) {
    forest.clear();
    cache.clear();
    constrs.clear();
    constrs.insert(constrs.end(), c1.constrs.begin(), c1.constrs.end());
    constrs.insert(constrs.end(), c2.constrs.begin(), c2.constrs.end());

    // create the terminal nodes
    forest.push_back(node_t(c1.forest[c1.F], c2.forest[c2.F])); // node 0
    forest.push_back(node_t(c1.forest[c1.T], c2.forest[c2.T])); // node 1

    // create the root nodes
    node_t root_node(c1.forest[c1.root_node_id], c2.forest[c2.root_node_id]);

    // do the intersection
    intersection_op_cache_t op_cache;
    root_node_id = intersect_recursive(c1, c2, c1.root_node_id, c2.root_node_id, op_cache);
}

//---------------------------------------------------------------------------------------

size_t CDD_t::intersect_recursive(const CDD_t& c1, const CDD_t& c2,
                                  const size_t node_id1, const size_t node_id2,
                                  intersection_op_cache_t& op_cache)
{
    if (node_id1 == c1.T)
        return node_id2 == c2.T ? T : F;
    else if (node_id1 == c1.F)
        return F;

    // Search the operation cache
    auto cache_key = make_pair(node_id1, node_id2);
    auto cache_id = op_cache.find(cache_key);
    if (cache_id != op_cache.end())
        return cache_id->second;

    node_t new_node(c1.forest[node_id1], c2.forest[node_id2]);
    const node_t& node1 = c1.forest[node_id1];
    const node_t& node2 = c2.forest[node_id2];

    if (new_node.level() != node2.level()) {
        for (const edge_t& ee1 : node1.ee) {
            size_t next_id = intersect_recursive(c1, c2, ee1.node_ref, node_id2, op_cache);
            if (next_id != F)
                new_node.ee.push_back(edge_t{.value=ee1.value, .node_ref=next_id});
        }
    }
    else if (new_node.level() != node1.level()) {
        for (const edge_t& ee2 : node2.ee) {
            size_t next_id = intersect_recursive(c1, c2, node_id1, ee2.node_ref, op_cache);
            if (next_id != F)
                new_node.ee.push_back(edge_t{.value=ee2.value, .node_ref=next_id});
        }
    }
    else {
        size_t i1 = 0, i2 = 0;
        while (i1 < node1.ee.size() && i2 < node2.ee.size()) {
            if (node1.ee[i1].value < node2.ee[i2].value)
                i1++;
            else if (node1.ee[i1].value > node2.ee[i2].value)
                i2++;
            else {
                // Generate the intersection node
                size_t next_id = intersect_recursive(c1, c2, node1.ee[i1].node_ref, node2.ee[i2].node_ref, op_cache);
                if (next_id != F)
                    new_node.ee.push_back(edge_t{.value=node1.ee[i1].value, .node_ref=next_id});
                i1++;
                i2++;
            }
        }
    }

    // Add the node to the forest and to the cache
    size_t node_id = add_node(std::move(new_node));
    op_cache.insert(make_pair(cache_key, node_id));

    return node_id;
}

//---------------------------------------------------------------------------------------

std::map<size_t, std::vector<bool>> CDD_t::mark_var_domains() const {
    // initialize the domain
    std::map<size_t, std::vector<bool>> level_doms;
    // mark all domain values used by the DD edges
    for (const node_t& node : forest) {
        if (node.is_terminal())
            continue;
        if (level_doms.count(node.level()) == 0) {
            // initialize the level
            const int plc = fbm.level_to_net[node.level()];
            const int bound = fbm.get_bound(plc);
            std::vector<bool> ld(bound + 1, false);
            level_doms[node.level()] = ld;
        }
        std::vector<bool>& doms = level_doms[node.level()];
        // cout << "marking node: " << node << endl;
        for (const edge_t& edge : node.ee)
            doms[edge.value] = true;
    }
    return level_doms;
}

//---------------------------------------------------------------------------------------

void CDD_t::intersect_var_domains(const std::vector<std::vector<bool>>& var_doms) {
    intersect_var_domains_cache_t op_cache;
    root_node_id = intersect_var_domains_recursive(var_doms, root_node_id, op_cache);
    collect_unused_nodes();
}

//---------------------------------------------------------------------------------------

size_t CDD_t::intersect_var_domains_recursive(const std::vector<std::vector<bool>>& var_doms,
                                              const size_t node_id,
                                              intersect_var_domains_cache_t& op_cache)
{
    if (node_id == T || node_id == F)
        return node_id; // nothing to do

    size_t cache_key = node_id;
    auto cache = op_cache.find(cache_key);
    if (cache != op_cache.end())
        return cache->second;

    node_t& node = forest[node_id];

    std::vector<edge_t> new_edges;
    for (const edge_t& edge : node.ee) {
        if (var_doms[node.level()][edge.value]) {
            size_t next_id = intersect_var_domains_recursive(var_doms, edge.node_ref, op_cache);
            if (next_id != F)
                new_edges.push_back(edge_t{.value=edge.value, .node_ref=next_id});
        }
    }
    std::swap(new_edges, node.ee);

    size_t new_node_id = node_id;
    if (node.ee.empty())
        new_node_id = F;
    
    // cout << "intersect dom: " << node << "  " << node_id << "->" << new_node_id << endl;
    op_cache.insert(make_pair(cache_key, new_node_id));

    return new_node_id;
}

//---------------------------------------------------------------------------------------






//---------------------------------------------------------------------------------------

std::vector<std::vector<bool>> initialize_level_domains(const flow_basis_metric_t& fbm) 
{
    std::vector<std::vector<bool>> dom(npl);
    for (size_t lvl=0; lvl<dom.size(); lvl++) {
        const int plc = fbm.level_to_net[lvl];
        const int bound = fbm.get_bound(plc);
        dom[lvl].resize(bound + 1, true);
    }
    return dom;
}

//---------------------------------------------------------------------------------------

void intersect_level_domains(std::vector<std::vector<bool>>& dom,
                             const std::map<size_t, std::vector<bool>>& level_doms) 
{
    for (auto iter : level_doms) {
        const size_t lvl = iter.first;
        assert(iter.second.size() == dom[lvl].size());
        for (size_t i=0; i<iter.second.size(); i++)
            if (!iter.second[i]) {
                dom[lvl][i] = false;
                // cout << "removing " << i << " from level " << lvl << endl;
            }
    }
}

//---------------------------------------------------------------------------------------

void show_level_domains(std::vector<std::vector<bool>>& dom) {
    for (ssize_t lvl = dom.size()-1; lvl >=0; lvl--) {
        cout << "LEVEL " << setw(2) << lvl << ": ";
        for (size_t i=0; i<dom[lvl].size(); i++) {
            // cout << " " << (dom[lvl][i] ? "T" : "F");
            if (dom[lvl][i])
                cout << " " << i;
        }
        cout << endl;
    }
}

//---------------------------------------------------------------------------------------

void experiment_cdd(const flow_basis_metric_t& fbm) {

    std::vector<std::vector<bool>> dom = initialize_level_domains(fbm);
    std::vector<std::unique_ptr<CDD_t>> constr_dd(fbm.B.size());

    for (size_t i=0; i<fbm.B.size(); i++) {
        constr_dd[i] = make_unique<CDD_t>(fbm, i);
        constr_dd[i]->initialize();
        constr_dd[i]->collect_unused_nodes();
        constr_dd[i]->show(cout);
        cout << endl;

        std::map<size_t, std::vector<bool>> level_doms;
        level_doms = constr_dd[i]->mark_var_domains();
        // for (auto iter : level_doms) {
        //     cout << "LVL " << iter.first << " : ";
        //     for (size_t i=0; i<iter.second.size(); i++)
        //         if (iter.second[i])
        //             cout << " " << i;
        //     cout << endl;
        // }
        // cout << endl;

        intersect_level_domains(dom, level_doms);
        // show_level_domains(dom);
        // cout << endl;
    }
    show_level_domains(dom);
    cout << endl;

    for (size_t i=0; i<fbm.B.size(); i++) {
        size_t sz1 = constr_dd[i]->num_nodes();
        constr_dd[i]->intersect_var_domains(dom);
        size_t sz2 = constr_dd[i]->num_nodes();
        cout << "constraint " << i << " from " << sz1 << " to " << sz2 << endl;
        // constr_dd[i]->show(cout);
        // cout << endl;
    }
    cout << "\n\n\n\n\n\n";
    // exit(0);



    unique_ptr<CDD_t> isect = std::move(constr_dd[0]);
    for (size_t i=1; i<fbm.B.size(); i++) {
        unique_ptr<CDD_t> isect_n = make_unique<CDD_t>(fbm, 0);
        isect_n->intersection(*isect, *constr_dd[i]);
        isect_n->collect_unused_nodes();
        isect_n->show(cout);
        cout << "Nodes: " << isect_n->num_nodes()-2 << endl;
        cout << "Edges: " << isect_n->num_edges() << endl;

        std::swap(isect_n, isect);
    }
}

//---------------------------------------------------------------------------------------

